package com.splitwiseapp.controller;

import com.splitwiseapp.dto.events.EventDto;
import com.splitwiseapp.dto.users.UserDto;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.service.events.EventService;
import com.splitwiseapp.service.expenses.ExpenseService;
import com.splitwiseapp.service.users.UserService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @GetMapping("/events")
    public String events(Model model) {
        List<Event> events = eventService.findAllEvents();
        model.addAttribute("events", events);
        return "events";
    }
    @GetMapping("/events/{eventId}")
    public String eventDetails(@PathVariable("eventId") Integer eventId,Model model) {
        Event event = eventService.findById(eventId);

        model.addAttribute("event", event);
        return "event";
    }

    @GetMapping("/newEvent")
    public String showEventAddingForm(Model model){
        List<UserDto> allUsers = userService.findAllUsers();
        model.addAttribute("newEvent", new EventDto());
        model.addAttribute("allUsers", allUsers);
        return "new-event";
    }

    @PostMapping("/saveEvent")
    public String addEvent(@ModelAttribute("events") EventDto eventDto,
                           BindingResult result,
                           Model model) {

        if (doesEventWithGivenNameAlreadyExist(eventDto)) {
            result.rejectValue("eventName", null,
                    "That event already exists or given name is incorrect.");
        }
        if (result.hasErrors()) {
            model.addAttribute("events", eventDto);
            return "redirect:/events";
        }

        Event event = Event.builder()
                .eventName(eventDto.getEventName())
                .owner(userService.getCurrentlyLoggedInUser())
                .build();

        event.addUser(userService.getCurrentlyLoggedInUser());
        event.setOwner(userService.getCurrentlyLoggedInUser());
        eventService.saveEvent(event);

        return "redirect:/events";
    }

    @GetMapping("/delete")
    public String deleteEvent(@RequestParam("eventId") Integer eventId) {
        eventService.deleteById(eventId);
        return "redirect:/events";
    }

    @GetMapping("/events/{eventId}/users")
    public String showEventUsers(@PathVariable("eventId") Integer eventId, Model model) {
        Event event = eventService.findById(eventId);
        List<User> allUsers = userService.findAll();
        List<User> eventUsers = event.getEventUsers();
        List<User> remainingUsers = new ArrayList<>();
        List<Expense> eventExpenses = expenseService.findExpensesForGivenEvent(eventId);

        model.addAttribute("event", event);

        for (User u: allUsers)
        {
            if (!eventUsers.contains(u)) {
                remainingUsers.add(u);
            }
        }

        model.addAttribute("add_id", eventId);
        model.addAttribute("remove_id", eventId);
        model.addAttribute("eventUsers", eventUsers);
        model.addAttribute("remainingUsers", remainingUsers);
        model.addAttribute("eventExpenses", eventExpenses);
        return "users";
    }

    @GetMapping("/events/{eventId}/expenses")
    public String showEventExpenses(@PathVariable("eventId") Integer eventId, Model model) {
        Event event = eventService.findById(eventId);
        List<User> allUsers = userService.findAll();
        List<User> eventUsers = event.getEventUsers();
        List<User> remainingUsers = new ArrayList<>();
        List<Expense> eventExpenses = expenseService.findExpensesForGivenEvent(eventId);
        Map<Integer, Map<Integer, BigDecimal>> expensePayoffsToParticipants = expenseService.mapExpenseToUserPayoffAmount(eventExpenses);
        model.addAttribute("expensePayoffsToParticipants", expensePayoffsToParticipants);

        model.addAttribute("event", event);

        for (User u: allUsers)
        {
            if (!eventUsers.contains(u)) {
                remainingUsers.add(u);
            }
        }

        System.out.println(expensePayoffsToParticipants);


        model.addAttribute("add_id", eventId);
        model.addAttribute("remove_id", eventId);
        model.addAttribute("eventUsers", eventUsers);
        model.addAttribute("remainingUsers", remainingUsers);
        model.addAttribute("eventExpenses", eventExpenses);
        return "expenses";
    }

    @GetMapping("/events/{eventId}/addUser")
    public String addUser(@PathVariable("eventId") Integer eventId, @RequestParam("userId") Integer userId) {
        Event event = eventService.findById(eventId);
        User user = userService.findById(userId);
        event.addUser(user);
        eventService.save(event);
        user.addEvent(event);
        userService.save(user);
        return "redirect:/events/" + eventId + "/users";
    }

    @GetMapping("/events/{eventId}/removeUser")
    public String removeUser(@PathVariable("eventId") Integer eventId, @RequestParam("userId") Integer userId) {
        Event event = eventService.findById(eventId);
        User user = userService.findById(userId);
        event.removeUser(user);
        eventService.save(event);
        user.removeEvent(event);
        userService.save(user);
        return "redirect:/events/" + eventId + "/users";
    }

    @GetMapping("/events/{eventId}/addExpense")
    public String addExpense(@PathVariable("eventId") Integer eventId, @RequestParam("expenseId") Integer expenseId) {
        Event event = eventService.findById(eventId);
        Expense expense = expenseService.findById(expenseId);
        event.addExpense(expense);
        eventService.save(event);
        expense.addEvent(event);
        expenseService.saveExpense(expense);
        return "redirect:/events/" + eventId + "/expenses";
    }

    @GetMapping("/events/{eventId}/removeExpense")
    public String removeExpense(@PathVariable("eventId") Integer eventId, @RequestParam("expenseId") Integer expenseId) {
        Event event = eventService.findById(eventId);
        Expense expense = expenseService.findById(expenseId);
        event.removeExpense(expense);
        eventService.save(event);
        expense.removeEvent();
        expenseService.saveExpense(expense);
        return "redirect:/events/" + eventId + "/expenses";
    }

    private boolean doesEventWithGivenNameAlreadyExist(EventDto eventDto) {
        Event event = eventService.findByEventName(eventDto.getEventName());
        return event != null && !StringUtils.isBlank(event.getEventName());
    }

}