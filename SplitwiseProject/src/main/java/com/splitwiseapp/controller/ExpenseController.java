package com.splitwiseapp.controller;

import com.splitwiseapp.dto.expense.ExpenseDto;
import com.splitwiseapp.dto.expense.ExpenseMapper;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.Payoff;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.ExpenseRepository;
import com.splitwiseapp.repository.UserRepository;
import com.splitwiseapp.service.events.EventService;
import com.splitwiseapp.service.expenses.ExpenseService;
import com.splitwiseapp.service.payoffs.PayoffService;
import com.splitwiseapp.service.users.UserService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Data
@Controller
public class ExpenseController {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ExpenseService expenseService;
    private final ExpenseRepository expenseRepository;
    private final PayoffService payoffService;
    private final ExpenseMapper expenseMapper;

    @GetMapping("/events/{eventId}/newExpense")
    public String showExpenseForm(@PathVariable Integer eventId, Model model) {
        Event event = eventService.findById(eventId);
        List<User> eventUsers = event.getEventUsers();

        model.addAttribute("event", event);
        model.addAttribute("eventUsers", eventUsers);
        model.addAttribute("newExpense", new ExpenseDto());
        return "new-expense";
    }

    @GetMapping("/events/{eventId}/expenses/{expenseId}/delete")
    public String deleteExpense(@PathVariable Integer eventId,
                                @PathVariable Integer expenseId) {
        Event foundEvent = eventService.findById(eventId);
        Expense foundExpense = expenseService.findById(expenseId);
        BigDecimal costPerParticipant = foundExpense.getCostPerParticipant();

        foundExpense.getParticipants().forEach(participant -> {
            participant.setBalance(userService.calculateUserBalance(participant.getId()).add(costPerParticipant));
            participant.getExpenses().removeIf(expense -> participant.getExpenses().contains(expense));
            userService.save(participant);
        });

        foundEvent.removeExpense(foundExpense);
        expenseService.deleteById(expenseId);
        return "redirect:/events/" + eventId + "/expenses";
    }

    @PostMapping("/events/{id}/saveExpense")
    public String createExpense(@ModelAttribute("expenses") ExpenseDto expenseDto,
                                @PathVariable Integer id,
                                BindingResult result,
                                Model model) {
        Event foundEvent = eventService.findById(id);
        User loggedInUser = userService.getCurrentlyLoggedInUser();

        Expense expense = expenseMapper.mapToDomain(foundEvent, expenseDto);
        TreeSet<User> expenseParticipants = userService.getUsersByNames(expenseDto);
        List<Payoff> expensePayoffs = new ArrayList<>();

        Payoff defaultPayoff = Payoff.builder()
                .expensePaid(expense)
                .userPaying(loggedInUser)
                .payoffAmount(BigDecimal.ZERO)
                .build();
        expensePayoffs.add(defaultPayoff);
        expense.setPayoffs(expensePayoffs);

        if (doesExpenseWithGivenNameAlreadyExist(expenseDto)) {
            result.rejectValue("name", "That expense already exists or given name is incorrect.",
                    "That expense already exists or given name is incorrect.");
        }
        if (result.hasErrors()) {
            model.addAttribute("expenses", expenseDto);
            return "redirect:/events/{id}/expenses";
        }

        expenseService.save(expense);

        model.addAttribute("expenseParticipants", expenseParticipants);
        return "redirect:/events/" + id + "/expenses";
    }

    @GetMapping("/events/{eventId}/expenses/{expenseId}/users/{userId}")
    public String assignPaidOffAmount(@PathVariable("eventId") Integer eventId,
                                      @PathVariable("expenseId") Integer expenseId,
                                      @PathVariable("userId") Integer userId,
                                      @RequestParam("paidOffAmount") String paidOffAmount,
                                      Model model) {
        Expense foundExpense = expenseService.findById(expenseId);
        User foundUser = userService.findById(userId);

        BigDecimal paidOffFromInput = paidOffAmount == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.CEILING)
                : new BigDecimal(paidOffAmount.replaceAll(",", ".")).setScale(2, RoundingMode.CEILING);
        BigDecimal userBalance = userService.calculateUserBalance(foundUser.getId()).add(paidOffFromInput);

        Payoff payoff = Payoff.builder()
                .expensePaid(foundExpense)
                .userPaying(foundUser)
                .payoffAmount(paidOffFromInput)
                .build();

        if (foundExpense.getTotalCost() != null) {
            foundExpense.setExpenseBalance(paidOffFromInput.subtract(foundExpense.getTotalCost()));
        }
        foundExpense.getPayoffs().add(payoff);
        foundUser.getPayoffs().add(payoff);
        foundUser.setBalance(userBalance);

        userService.save(foundUser);
        expenseService.save(foundExpense);
        payoffService.save(payoff);

        model.addAttribute("userBalance", userBalance);

        return "redirect:/events/" + eventId + "/expenses";
    }

    @GetMapping("/expenses/{expenseId}/addUser")
    public String addUser(@PathVariable("expenseId") Integer expenseId, @RequestParam("userId") Integer userId) {
        Expense expense = expenseService.findById(expenseId);
        User user = userService.findById(userId);
        expense.addParticipant(user);
        expenseService.save(expense);
        user.addExpense(expense);
        userService.save(user);
        return "redirect:/expenses/" + expenseId + "/users";
    }

    @GetMapping("/expenses/{expenseId}/removeUser")
    public String removeUser(@PathVariable("expenseId") Integer expenseId, @RequestParam("userId") Integer userId) {
        Expense expense = expenseService.findById(expenseId);
        User user = userService.findById(userId);
        expense.removeParticipant(user);
        expenseService.save(expense);
        user.removeExpense(expense);
        userService.save(user);
        return "redirect:/expenses/" + expenseId + "/users";
    }

    private boolean doesExpenseWithGivenNameAlreadyExist(ExpenseDto expenseDto) {
        Expense expense = expenseRepository.findByName(expenseDto.getName());
        return expense != null && !StringUtils.isBlank(expense.getName());
    }

}
