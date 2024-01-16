package com.splitwiseapp.controller;

import com.splitwiseapp.dto.event.EventDto;
import com.splitwiseapp.dto.event.EventMapper;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.service.events.EventService;
import com.splitwiseapp.service.expenses.ExpenseService;
import com.splitwiseapp.service.users.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final ExpenseService expenseService;
    private final EventMapper eventMapper;

    @GetMapping("/events")
    public String events(Model model) {
        List<Event> events = eventService.findAllEvents();
        model.addAttribute("events", events);
        model.addAttribute("loggedInUserName", userService.getCurrentlyLoggedInUser().getUsername());
        return "events";
    }

    @GetMapping("/events/{eventId}")
    public String eventDetails(@PathVariable("eventId") Integer eventId, Model model) {
        Event event = eventService.findById(eventId);
        model.addAttribute("event", event);
        return "event";
    }


    @GetMapping("/newEvent")
    public String showEventAddingForm(Model model) {
        List<User> allUsers = userService.findAllUsers();
        model.addAttribute("newEvent", new EventDto());
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("loggedInUserName", userService.getCurrentlyLoggedInUser().getUsername());
        return "new-event";
    }

    @PostMapping("/newEvent")
    public String createEvent(@ModelAttribute("newEvent") EventDto eventDto,
                              Model model,
                              BindingResult result) {
        Event existingEvent = eventService.findByEventName(eventDto.getEventName());

        if (doesEventAlreadyExist(existingEvent)) {
            result.addError(new FieldError("newEvent", "eventName",
                    "Event '" + existingEvent.getEventName() + "' already exists."));
        }

        if (eventDto.getEventName().isBlank()) {
            result.addError(new FieldError("newEvent", "eventName",
                    "Event name field cannot be empty."));
        }

        if (result.hasErrors()) {
            return "new-event";
        }

        eventService.save(eventMapper.mapToDomain(eventDto));
        model.addAttribute("loggedInUserName", userService.getCurrentlyLoggedInUser().getUsername());
        return "redirect:/events";
    }

    @GetMapping("/delete")
    public String deleteEvent(@RequestParam("eventId") Integer eventId, Model model) {
        Event event = eventService.findById(eventId); //TODO: Przy kasowaniu eventu, leci NoSuchElementException - obsłużyć jakoś
        User user = userService.getCurrentlyLoggedInUser();

        if (event.getEventBalance() != null && event.getEventBalance().compareTo(BigDecimal.ZERO) < 0) {
            model.addAttribute("deleteError", "You cannot delete an unsettled event.");
        } else if (event.getOwner().equals(user)) {
            expenseService.deleteByEventId(eventId);
            eventService.deleteById(eventId);
        }

        List<Event> events = eventService.findAllEvents();
        model.addAttribute("events", events);
        model.addAttribute("loggedInUserName", user.getUsername());
        return "events";
    }

    @GetMapping("/events/{eventId}/users")
    public String showEventUsers(@PathVariable("eventId") Integer eventId, Model model) {
        Event event = eventService.findById(eventId);
        List<User> allUsers = userService.findAll();
        List<User> eventMembers = event.getEventMembers();
        List<User> remainingUsers = new ArrayList<>();
        List<Expense> eventExpenses = expenseService.findExpensesForGivenEvent(eventId);

        model.addAttribute("event", event);

        for (User u : allUsers) {
            if (!eventMembers.contains(u)) {
                remainingUsers.add(u);
            }
        }

        model.addAttribute("add_id", eventId);
        model.addAttribute("remove_id", eventId);
        model.addAttribute("eventMembers", eventMembers);
        model.addAttribute("remainingUsers", remainingUsers);
        model.addAttribute("eventExpenses", eventExpenses);
        model.addAttribute("loggedInUserName", userService.getCurrentlyLoggedInUser().getUsername());
        return "users";
    }

    @GetMapping("/events/{eventId}/expenses")
    public String showEventExpenses(@PathVariable("eventId") Integer eventId, Model model) {
        Event event = eventService.findById(eventId);
        List<User> allUsers = userService.findAll();
        List<User> eventMembers = event.getEventMembers();
        List<User> remainingUsers = new ArrayList<>();
        List<Expense> eventExpenses = expenseService.findExpensesForGivenEvent(eventId);
        User user = userService.getCurrentlyLoggedInUser();

        for (Expense expense : eventExpenses) {
            Map<Integer, BigDecimal> payoffAmountPerParticipant = expenseService.mapUserToPayoffAmount(expense);
            Map<Integer, BigDecimal> balancePerParticipant = expenseService.mapUserToBalance(expense);
            expense.setPayoffAmountPerUser(payoffAmountPerParticipant);
            expense.setBalancePerUser(balancePerParticipant);
        }

        for (User u : allUsers) {
            if (!eventMembers.contains(u)) {
                remainingUsers.add(u);
            }
        }

        BigDecimal updatedBalance = calculateUpdatedBalanceForEvent(eventExpenses);
        event.setEventBalance(updatedBalance);
        eventService.save(event);

        model.addAttribute("user", user);
        model.addAttribute("event", event);
        model.addAttribute("add_id", eventId);
        model.addAttribute("remove_id", eventId);
        model.addAttribute("eventMembers", eventMembers);
        model.addAttribute("remainingUsers", remainingUsers);
        model.addAttribute("eventExpenses", eventExpenses);
        model.addAttribute("updatedBalance", updatedBalance);
        model.addAttribute("loggedInUserName", userService.getCurrentlyLoggedInUser().getUsername());
        return "expenses";
    }

    @GetMapping("/events/{eventId}/addUser")
    public String addUser(@PathVariable("eventId") Integer eventId, @RequestParam("userId") Integer userId) {
        Event event = eventService.findById(eventId);
        User user = userService.findById(userId);
        event.addEventMember(user);
        eventService.save(event);
        user.addEvent(event);
        userService.save(user);
        return "redirect:/events/" + eventId + "/users";
    }

    @GetMapping("/events/{eventId}/removeUser")
    public String removeUser(@PathVariable("eventId") Integer eventId, @RequestParam("userId") Integer userId) {
        Event event = eventService.findById(eventId);
        User user = userService.findById(userId);
        event.removeEventMember(user);
        eventService.save(event);
        user.removeEvent(event);
        userService.save(user);
        return "redirect:/events/" + eventId + "/users";
    }

    @GetMapping("/events/{eventId}/setAsEventOwner/{userId}")
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

    @GetMapping("/events/{eventId}/addExpense")
    public String addExpense(@PathVariable("eventId") Integer eventId, @RequestParam("expenseId") Integer expenseId) {
        Event event = eventService.findById(eventId);
        Expense expense = expenseService.findById(expenseId);
        event.addExpense(expense);
        eventService.save(event);
        expense.addEvent(event);
        expenseService.save(expense);
        return "redirect:/events/" + eventId + "/expenses";
    }

    @GetMapping("/events/{eventId}/removeExpense")
    public String removeExpense(@PathVariable("eventId") Integer eventId, @RequestParam("expenseId") Integer expenseId) {
        Event event = eventService.findById(eventId);
        Expense expense = expenseService.findById(expenseId);
        event.removeExpense(expense);
        eventService.save(event);
        expense.removeEvent();
        expenseService.save(expense);
        return "redirect:/events/" + eventId + "/expenses";
    }

    private BigDecimal calculateUpdatedBalanceForEvent(List<Expense> eventExpenses) {
        return eventExpenses.stream()
                .flatMap(expense -> expense.getBalancePerUser().values().stream())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean doesEventAlreadyExist(Event existingEvent) {
        return existingEvent != null && !existingEvent.getEventName().isBlank();
    }

}