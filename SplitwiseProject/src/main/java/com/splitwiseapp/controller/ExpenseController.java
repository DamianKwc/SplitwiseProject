package com.splitwiseapp.controller;

import com.splitwiseapp.dto.expenses.ExpenseDto;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.ExpenseRepository;
import com.splitwiseapp.repository.UserRepository;
import com.splitwiseapp.service.events.EventService;
import com.splitwiseapp.service.expenses.ExpenseService;
import com.splitwiseapp.service.users.UserService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigDecimal;

@Data
@Controller
public class ExpenseController {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ExpenseService expenseService;
    private final ExpenseRepository expenseRepository;

    @GetMapping("/events/{eventId}/newExpense")
    public String showExpenseForm(@PathVariable Integer eventId, Model model) {
        Event event = eventService.findById(eventId);
        User user = event.getOwner();

        model.addAttribute("event", event);
        model.addAttribute("user", user);
        model.addAttribute("newExpense", new ExpenseDto());

        return "new-expense";
    }

    @PostMapping("/events/{id}/saveExpense")
    public String createExpense(@ModelAttribute("expenses") ExpenseDto expenseDto,
                                @PathVariable Integer id,
                                BindingResult result,
                                Model model) {

        if (doesExpenseWithGivenNameAlreadyExist(expenseDto)) {
            result.rejectValue("expenseName", null,
                    "That expense already exists or given name is incorrect.");
        }
        if (result.hasErrors()) {
            model.addAttribute("expenses", expenseDto);
            return "redirect:/events/{id}/users";
        }

        Expense expense = Expense.builder()
                .expenseName(expenseDto.getExpenseName())
                .expenseAmount(new BigDecimal(expenseDto.getExpenseAmount().replaceAll(",", ".")))
                .event(eventService.findById(id))
                .user(userService.getCurrentlyLoggedInUser())
                .build();

        expenseService.saveExpense(expense);

        return "redirect:/events/" + id + "/users";
    }

    private boolean doesExpenseWithGivenNameAlreadyExist(ExpenseDto expenseDto) {
        Expense expense = expenseRepository.findByExpenseName(expenseDto.getExpenseName());
        return expense != null && !StringUtils.isBlank(expense.getExpenseName());
    }

}
