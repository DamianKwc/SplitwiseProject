package com.splitwiseapp.controller;

import com.splitwiseapp.dto.expense.CustomExpenseDto;
import com.splitwiseapp.dto.expense.SplitExpenseDto;
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
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    @GetMapping("/events/{eventId}/newSplitExpense")
    public String showSplitExpenseForm(@PathVariable Integer eventId, Model model) {
        Event event = eventService.findById(eventId);
        List<User> eventMembers = event.getEventMembers();

        model.addAttribute("event", event);
        model.addAttribute("eventMembers", eventMembers);
        model.addAttribute("newSplitExpense", new SplitExpenseDto());
        return "new-split-expense";
    }

    @GetMapping("/events/{eventId}/newCustomExpense")
    public String showCustomExpenseForm(@PathVariable Integer eventId, Model model) {
        Event event = eventService.findById(eventId);
        List<User> eventMembers = event.getEventMembers();

        model.addAttribute("event", event);
        model.addAttribute("eventMembers", eventMembers);
        model.addAttribute("newCustomExpense", new CustomExpenseDto());
        model.addAttribute("loggedInUserName", userService.getCurrentlyLoggedInUser().getUsername());
        return "new-custom-expense";
    }

    @GetMapping("/events/{eventId}/expenses/{expenseId}/delete")
    public String deleteExpense(@PathVariable Integer eventId,
                                @PathVariable Integer expenseId,
                                Model model) {
        Event foundEvent = eventService.findById(eventId);
        Expense foundExpense = expenseService.findById(expenseId);
        Map<Integer, BigDecimal> costPerParticipant = foundExpense.getCostPerUser();

        foundExpense.getParticipants().forEach(participant -> {
            participant.setBalance(userService.calculateUserBalance(participant.getId()).add(costPerParticipant.get(participant.getId())));
            participant.getExpenses().removeIf(expense -> participant.getExpenses().contains(expense));
            userService.save(participant);
        });

        foundEvent.removeExpense(foundExpense);
        expenseService.deleteById(expenseId);
        model.addAttribute("loggedInUserName", userService.getCurrentlyLoggedInUser().getUsername());
        return "redirect:/events/" + eventId + "/expenses";
    }

    @PostMapping("/events/{eventId}/saveSplitExpense")
    public String createSplitExpense(@ModelAttribute("newSplitExpense") SplitExpenseDto splitExpenseDto,
                                     @PathVariable Integer eventId,
                                     BindingResult result,
                                     Model model) {
        Event foundEvent = eventService.findById(eventId);
        model.addAttribute("event", foundEvent);

        User loggedInUser = userService.getCurrentlyLoggedInUser();
        model.addAttribute("loggedInUserName", loggedInUser.getUsername());

        List<User> eventMembers = foundEvent.getEventMembers();
        model.addAttribute("eventMembers", eventMembers);

        List<User> expenseParticipants = userService.getUsersByNames(splitExpenseDto);
        model.addAttribute("expenseParticipants", expenseParticipants);

        checkIfExpenseAlreadyExists(splitExpenseDto, eventId, result);

        if (splitExpenseDto.getName().isBlank()) {
            result.addError(new FieldError("newExpense", "name",
                    "Expense name field cannot be empty."));
        }

        Pattern pattern = Pattern.compile("[0-9]+(\\.[0-9]{1,2})?");
        Matcher matcher = pattern.matcher(splitExpenseDto.getCost());

        if (!matcher.matches()) {
            result.addError(new FieldError("newExpense", "cost",
                    "Enter proper value for expense amount."));
        }
        if (splitExpenseDto.getCost().isEmpty()) {
            result.addError(new FieldError("newExpense", "cost",
                    "Expense amount field cannot be empty."));
        }

        if (result.hasErrors()) {
            return "new-split-expense";
        }
        Expense expense = expenseMapper.mapSplitExpenseToDomain(foundEvent, splitExpenseDto);

        List<Payoff> expensePayoffs = new ArrayList<>();

        Payoff defaultPayoff = Payoff.builder()
                .expensePaid(expense)
                .userPaying(loggedInUser)
                .payoffAmount(BigDecimal.ZERO)
                .build();
        expensePayoffs.add(defaultPayoff);
        expense.setPayoffs(expensePayoffs);

        expenseService.save(expense);
        return "redirect:/events/" + eventId + "/expenses";
    }

    @PostMapping("/events/{eventId}/saveCustomExpense")
    public String createCustomExpense(@ModelAttribute("newCustomExpense") CustomExpenseDto customExpenseDto,
                                      @PathVariable Integer eventId,
                                      BindingResult result,
                                      Model model) {
        Event foundEvent = eventService.findById(eventId);
        model.addAttribute("event", foundEvent);

        User loggedInUser = userService.getCurrentlyLoggedInUser();
        model.addAttribute("loggedInUserName", loggedInUser.getUsername());

        List<User> eventMembers = foundEvent.getEventMembers();
        model.addAttribute("eventMembers", eventMembers);

        List<User> expenseParticipants = userService.getUsersByNames(customExpenseDto);
        model.addAttribute("expenseParticipants", expenseParticipants);

        checkIfExpenseAlreadyExists(customExpenseDto, eventId, result);

        if (customExpenseDto.getName().isBlank()) {
            result.addError(new FieldError("newExpense", "name",
                    "Expense name field cannot be empty."));
        }

        Pattern pattern = Pattern.compile("[0-9]+(\\.[0-9]{1,2})?");
        Matcher matcher = pattern.matcher(customExpenseDto.getCost());

        if (customExpenseDto.getCost().isEmpty()) {
            result.addError(new FieldError("newExpense", "cost",
                    "Expense amount field cannot be empty."));
        }

        if (!matcher.matches()) {
            result.addError(new FieldError("newExpense", "cost",
                    "Enter proper value for expense amount."));
        }

        if (result.hasErrors()) {
            return "new-custom-expense";
        }
//TODO validate żeby user nie mógł wpisać znaków w userContribute
        List<String> namesOfMembers = eventMembers.stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        customExpenseDto.setParticipantsNames(namesOfMembers);
        Expense expense = expenseMapper.mapCustomExpenseDtoToDomain(foundEvent, customExpenseDto);

        List<Payoff> expensePayoffs = new ArrayList<>();
        Payoff defaultPayoff = Payoff.builder()
                .expensePaid(expense)
                .userPaying(loggedInUser)
                .payoffAmount(BigDecimal.ZERO)
                .build();
        expensePayoffs.add(defaultPayoff);
        expense.setPayoffs(expensePayoffs);

        expenseService.save(expense);
        return "redirect:/events/" + eventId + "/expenses";
    }

    @GetMapping("/events/{eventId}/expenses/{expenseId}/users/{userId}")
    public String assignPaidOffAmount(@PathVariable("eventId") Integer eventId,
                                      @PathVariable("expenseId") Integer expenseId,
                                      @PathVariable("userId") Integer userId,
                                      @RequestParam("paidOffAmount") String paidOffAmount,
                                      Model model) {
        Expense foundExpense = expenseService.findById(expenseId);
        User foundUser = userService.findById(userId);

        model.addAttribute("paidOffAmount", paidOffAmount);

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

    private void checkIfExpenseAlreadyExists(@ModelAttribute("newSplitExpense") SplitExpenseDto splitExpenseDto,
                                             @PathVariable Integer eventId,
                                             BindingResult result) {
        Optional<Expense> existingExpense = Optional.ofNullable(expenseService.findByExpenseNameAndEventId(splitExpenseDto.getName(), eventId));
        existingExpense.ifPresent(expense -> result.addError(new FieldError("newExpense", "name",
                "Expense '" + expense.getName() + "' already exists.")));
    }

    private void checkIfExpenseAlreadyExists(@ModelAttribute("newCustomExpense") CustomExpenseDto customExpenseDto,
                                             @PathVariable Integer eventId,
                                             BindingResult result) {
        Optional<Expense> existingExpense = Optional.ofNullable(expenseService.findByExpenseNameAndEventId(customExpenseDto.getName(), eventId));
        existingExpense.ifPresent(expense -> result.addError(new FieldError("newExpense", "name",
                "Expense '" + expense.getName() + "' already exists.")));
    }

}
