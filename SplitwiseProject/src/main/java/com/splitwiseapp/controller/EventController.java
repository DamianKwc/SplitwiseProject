package com.splitwiseapp.controller;

import com.splitwiseapp.dto.event.EventDto;
import com.splitwiseapp.dto.event.EventMapper;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.exception.EventNotFoundException;
import com.splitwiseapp.exception.UserNotFoundException;
import com.splitwiseapp.service.events.EventService;
import com.splitwiseapp.service.expenses.ExpenseService;
import com.splitwiseapp.service.users.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@Controller
public class EventController {
    private final EventService eventService;
    private final UserService userService;
    private final ExpenseService expenseService;
    private final EventMapper eventMapper;

    @GetMapping("/events")
    public String events(@RequestParam(name = "eventName", required = false) String eventName,
                         Model model,
                         @AuthenticationPrincipal UserDetails userDetails) {
        String loggedInUsername = userDetails.getUsername();
        User loggedInUser = userService.findByUsername(loggedInUsername)
                .orElseThrow(() -> new UserNotFoundException("Currently logged in user not found."));

        Optional<List<Event>> eventsOptional = eventService.findEventsByUser(loggedInUser);
        List<Event> events = eventsOptional.orElseThrow(() -> new EventNotFoundException("Events not found for the user"));

        model.addAttribute("events", events);
        model.addAttribute("loggedInUserName", loggedInUsername);
        return "events";
    }

    @GetMapping("/events/{eventId}")
    public String eventDetails(@PathVariable("eventId") Integer eventId,
                               Model model) {
        Event event = eventService.findById(eventId);
        model.addAttribute("event", event);
        return "event";
    }

    @GetMapping("/newEvent")
    public String showEventAddingForm(Model model,
                                      @AuthenticationPrincipal UserDetails userDetails) {
        List<User> allUsers = userService.findAll();
        model.addAttribute("newEvent", new EventDto());
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("loggedInUserName", userDetails.getUsername());
        return "new-event";
    }

    @PostMapping("/newEvent") //TODO: coś się popsuło przez optionala w event repository i można stworzyć event o tej samej nazwie dla jednego usera
    public String createEvent(@ModelAttribute("newEvent") EventDto eventDto,
                              BindingResult result,
                              @AuthenticationPrincipal UserDetails userDetails) {
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());
        User user = optionalUser.orElseThrow(() ->
                new UserNotFoundException("User not found with username: " + userDetails.getUsername()));

        Optional<Event> existingEvent = eventService.findByEventNameAndOwner(eventDto.getEventName(), user);

        if (existingEvent.isPresent()) {
            result.addError(new FieldError("newEvent", "eventName",
                    "You already have an event with the name '" + eventDto.getEventName() + "'. Please choose a different name."));
        }

        if (eventDto.getEventName().isBlank()) {
            result.addError(new FieldError("newEvent", "eventName",
                    "Event name field cannot be blank."));
        }

        if (result.hasErrors()) {
            return "new-event";
        }

        eventService.save(eventMapper.mapToDomain(eventDto, userDetails));
        return "redirect:/events";
    }

    @DeleteMapping("/deleteEvent")
    public String deleteEvent(@RequestParam("eventId") Integer eventId,
                              RedirectAttributes redirectAttributes,
                              @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + userDetails.getUsername()));
        Event event = eventService.findById(eventId);

        if (event.getEventBalance() != null && event.getEventBalance().compareTo(BigDecimal.ZERO) < 0) {
            redirectAttributes.addFlashAttribute("deleteError", "You cannot delete an unsettled event.");
        } else if (event.getOwner().equals(user)) {
            expenseService.deleteByEventId(eventId);
            eventService.deleteById(eventId);
        }

        return "redirect:/events";
    }

    @GetMapping("/events/{eventId}/users")
    public String showEventUsers(@PathVariable("eventId") Integer eventId,
                                 Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User loggedInUser = getLoggedInUser(userDetails);
        Event event = eventService.findById(eventId);
        List<User> allUsers = userService.findAll();
        List<User> eventMembers = event.getEventMembers();
        List<User> remainingUsers = new ArrayList<>();
        List<Expense> eventExpenses = expenseService.findExpensesForGivenEvent(eventId);

        populateUserLists(allUsers, eventMembers, remainingUsers);

        model.addAttribute("event", event);
        saveCommonAttributes(model, eventId, eventMembers, remainingUsers, eventExpenses, loggedInUser);
        return "users";
    }

    @GetMapping("/events/{eventId}/expenses")
    public String showEventExpenses(@PathVariable("eventId") Integer eventId,
                                    @RequestParam(value = "errorMessage", required = false) String errorMessage,
                                    Model model,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        User loggedInUser = getLoggedInUser(userDetails);

        Event event = eventService.findById(eventId);
        List<User> allUsers = userService.findAll();
        List<User> eventMembers = event.getEventMembers();
        List<User> remainingUsers = new ArrayList<>();
        List<Expense> eventExpenses = expenseService.findExpensesForGivenEvent(eventId);

        updateExpenseAttributes(eventExpenses);
        populateUserLists(allUsers, eventMembers, remainingUsers);
        BigDecimal updatedBalance = calculateUpdatedBalanceForEvent(eventExpenses);
        event.setEventBalance(updatedBalance);
        eventService.save(event);

        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("event", event);
        saveCommonAttributes(model, eventId, eventMembers, remainingUsers, eventExpenses, loggedInUser);
        model.addAttribute("updatedBalance", updatedBalance);
        return "expenses";
    }

    @PostMapping("/events/{eventId}/addUser")
    public String addUser(@PathVariable("eventId") Integer eventId,
                          @RequestParam("userId") Integer userId) {
        Event event = eventService.findById(eventId);
        User user = userService.findById(userId);
        event.addEventMember(user);
        eventService.save(event);
        user.addEvent(event);
        userService.save(user);
        return "redirect:/events/" + eventId + "/users";
    }

    @DeleteMapping("/events/{eventId}/removeUser")
    public String removeUser(@PathVariable("eventId") Integer eventId,
                             @RequestParam("userId") Integer userId) {
        Event event = eventService.findById(eventId);
        User user = userService.findById(userId);
        event.removeEventMember(user);
        eventService.save(event);
        user.removeEvent(event);
        userService.save(user);
        return "redirect:/events/" + eventId + "/users";
    }

    @PostMapping("/events/{eventId}/setAsEventOwner/{userId}")
    public String setAsEventOwner(@PathVariable("eventId") Integer eventId,
                                  @PathVariable("userId") Integer userId,
                                  Model model) {
        Event event = eventService.findById(eventId);
        User user = userService.findById(userId);
        event.setOwner(user);
        eventService.save(event);
        model.addAttribute("loggedInUserName", user.getUsername());
        return "redirect:/events";
    }

    private BigDecimal calculateUpdatedBalanceForEvent(List<Expense> eventExpenses) {
        return eventExpenses.stream()
                .flatMap(expense -> expense.getBalancePerUser().values().stream())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    private void populateUserLists(List<User> allUsers, List<User> eventMembers, List<User> remainingUsers) {
        for (User u : allUsers) {
            if (!eventMembers.contains(u)) {
                remainingUsers.add(u);
            }
        }
    }

    private User getLoggedInUser(UserDetails userDetails) {
        return userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + userDetails.getUsername()));
    }

    private void updateExpenseAttributes(List<Expense> eventExpenses) {
        for (Expense expense : eventExpenses) {
            Map<Integer, BigDecimal> payoffAmountPerParticipant = expenseService.mapUserToPayoffAmount(expense);
            Map<Integer, BigDecimal> balancePerParticipant = expenseService.mapUserToBalance(expense);
            Map<Integer, BigDecimal> costPerParticipant = expenseService.mapUserToCost(expense);
            expense.setCostPerUser(costPerParticipant);
            expense.setPayoffPerUser(payoffAmountPerParticipant);
            expense.setBalancePerUser(balancePerParticipant);
            expenseService.save(expense);
        }
    }

    private void saveCommonAttributes(Model model, Integer eventId, List<User> eventMembers, List<User> remainingUsers, List<Expense> eventExpenses, User loggedInUser) {
        model.addAttribute("add_id", eventId);
        model.addAttribute("remove_id", eventId);
        model.addAttribute("eventMembers", eventMembers);
        model.addAttribute("remainingUsers", remainingUsers);
        model.addAttribute("eventExpenses", eventExpenses);
        model.addAttribute("loggedInUserName", loggedInUser.getUsername());
    }
}