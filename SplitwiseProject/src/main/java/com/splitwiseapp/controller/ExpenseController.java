package com.splitwiseapp.controller;

import com.splitwiseapp.dto.expenses.ExpenseDto;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.Payoff;
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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        List<User> eventUsers = event.getEventUsers();

        model.addAttribute("event", event);
        model.addAttribute("eventUsers", eventUsers);
        model.addAttribute("newExpense", new ExpenseDto());
        return "new-expense";
    }

    @GetMapping("/events/{eventId}/expenses/{expenseId}/delete")
    public String deleteExpense(@PathVariable Integer expenseId,
                                @PathVariable Integer eventId) {
        expenseService.deleteById(expenseId);
        return "redirect:/events/" + eventId + "/expenses";
    }

    @PostMapping("/events/{id}/saveExpense")
    public String createExpense(@ModelAttribute("expenses") ExpenseDto expenseDto,
                                @PathVariable Integer id,
                                BindingResult result,
                                Model model) {
        Set<User> participants = getUsers(expenseDto);
        BigDecimal cost = expenseDto.getCost() == null
                ? BigDecimal.ZERO
                : new BigDecimal(expenseDto.getCost().replaceAll(",", "."));
        BigDecimal costPerParticipant = expenseService.splitCostEquallyPerParticipants(cost, participants.size());

        model.addAttribute("costParticipants", participants);

        if (doesExpenseWithGivenNameAlreadyExist(expenseDto)) {
            result.rejectValue("name", "That expense already exists or given name is incorrect.",
                    "That expense already exists or given name is incorrect.");
        }
        if (result.hasErrors()) {
            model.addAttribute("expenses", expenseDto);
            return "redirect:/events/{id}/expenses";
        }

        participants.forEach(user -> {
            user.setUserDebt(costPerParticipant);
            user.setBalance(BigDecimal.ZERO.subtract(costPerParticipant));
        });

        Expense expense = Expense.builder()
                .name(expenseDto.getName())
                .totalCost(cost)
                .equalSplit(costPerParticipant)
                .event(eventService.findById(id))
                .participants(participants)
                .build();

        expenseService.saveExpense(expense);

        return "redirect:/events/" + id + "/expenses";
    }

    @GetMapping("/expenses/{expenseId}/addUser")
    public String addUser(@PathVariable("expenseId") Integer expenseId, @RequestParam("userId") Integer userId) {
        Expense expense = expenseService.findById(expenseId);
        User user = userService.findById(userId);
        expense.addParticipant(user);
        expenseService.saveExpense(expense);
        user.addExpense(expense);
        userService.save(user);
        return "redirect:/expenses/" + expenseId + "/users";
    }

    @GetMapping("/expenses/{expenseId}/removeUser")
    public String removeUser(@PathVariable("expenseId") Integer expenseId, @RequestParam("userId") Integer userId) {
        Expense expense = expenseService.findById(expenseId);
        User user = userService.findById(userId);
        expense.removeParticipant(user);
        expenseService.saveExpense(expense);
        user.removeExpense(expense);
        userService.save(user);
        return "redirect:/expenses/" + expenseId + "/users";
    }

    @GetMapping("/events/{eventId}/expenses/{expenseId}/users/{userId}")
    public String assignPaidOffAmount(@PathVariable("eventId") Integer eventId,
                                      @PathVariable("expenseId") Integer expenseId,
                                      @PathVariable("userId") Integer userId,
                                      @RequestParam("paidOffAmount") String paidOffAmount) {

        List<Payoff> payoffs = new ArrayList<>();
        Expense foundExpense = expenseService.findById(expenseId);
        User foundUser = userService.findById(userId);

        BigDecimal paidOffFromInput = paidOffAmount == null
                ? BigDecimal.ZERO
                : new BigDecimal(paidOffAmount.replaceAll(",", "."));

        payoffs.add(Payoff.builder()
                .expensePaid(foundExpense)
                .userPaying(foundUser)
                .payoffAmount(paidOffFromInput)
                .build());

        if (foundExpense.getTotalCost() != null) {
            foundExpense.setExpenseBalance(paidOffFromInput.subtract(foundExpense.getTotalCost()));
        }
        foundExpense.setPayoffs(payoffs);
        expenseService.saveExpense(foundExpense);

        if (foundUser.getBalance() == null) {
            foundUser.setBalance(BigDecimal.ZERO);
        } else {
            foundUser.setBalance(foundUser.getBalance().add(paidOffFromInput));
        }
        userService.save(foundUser);

        return "redirect:/events/" + eventId + "/expenses";
    }

    private Set<User> getUsers(ExpenseDto expenseDto) {
        Set<User> participants = new HashSet<>();
        String[] splitUsernames = expenseDto.getParticipantUsername().split("[,]", 0);
        for (String username : splitUsernames) {
            User foundUser = userService.findByUsername(username);
            participants.add(foundUser);
        }
        return participants;
    }

    private boolean doesExpenseWithGivenNameAlreadyExist(ExpenseDto expenseDto) {
        Expense expense = expenseRepository.findByName(expenseDto.getName());
        return expense != null && !StringUtils.isBlank(expense.getName());
    }

}
