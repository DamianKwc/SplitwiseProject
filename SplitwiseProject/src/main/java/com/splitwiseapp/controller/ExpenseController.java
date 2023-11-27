package com.splitwiseapp.controller;

import com.splitwiseapp.dto.events.EventDto;
import com.splitwiseapp.dto.expenses.ExpenseDto;
import com.splitwiseapp.dto.users.UserDto;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.ExpenseRepository;
import com.splitwiseapp.repository.UserRepository;
import com.splitwiseapp.service.events.EventService;
import com.splitwiseapp.service.expenses.ExpenseService;
import com.splitwiseapp.service.users.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import static com.splitwiseapp.shared.UserUtils.getCurrentlyLoggedInUser;

@Controller
@AllArgsConstructor
public class ExpenseController {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ExpenseService expenseService;
    private final ExpenseRepository expenseRepository;

    @GetMapping("/newExpense")
    public String showExpenseAddingForm(Model model){
        List<UserDto> allUsers = userService.findAllUsers();
        List<Event> allEvents = eventService.findAllEvents();

        model.addAttribute("newExpense", new ExpenseDto());
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("allEvents", allEvents);
        return "new-expense";
    }

}
