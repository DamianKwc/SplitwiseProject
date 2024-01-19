package com.splitwiseapp.dto.expense;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.service.expenses.ExpenseService;
import com.splitwiseapp.service.users.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ExpenseMapper {

    private final UserService userService;
    private final ExpenseService expenseService;

    public Expense mapSplitExpenseToDomain(Event event, SplitExpenseDto splitExpenseDto) {
        TreeSet<User> expenseParticipants = userService.getUsersByNames(splitExpenseDto);
        BigDecimal cost = splitExpenseDto.getCost().isBlank()
                ? BigDecimal.ZERO
                : new BigDecimal(splitExpenseDto.getCost().replaceAll(",", "."));
        BigDecimal costPerParticipant = expenseService.splitCostEquallyPerParticipants(cost, expenseParticipants.size());

        expenseParticipants.forEach(participant -> {
            List<String> namesOfExistingExpenses = participant.getExpenses().stream()
                    .map(Expense::getName)
                    .collect(Collectors.toList());
            if (!namesOfExistingExpenses.contains(splitExpenseDto.getName())) {
                participant.setBalance(userService.calculateUserBalance(participant.getId()).subtract(costPerParticipant));
            }
            userService.save(participant);
        });

        return Expense.builder()
                .name(splitExpenseDto.getName())
                .totalCost(cost)
                .costPerParticipant(costPerParticipant)
                .event(event)
                .participants(expenseParticipants)
                .payoffAmountPerUser(new HashMap<>())
                .balancePerUser(new HashMap<>())
                .build();
    }

    public Expense mapCustomExpenseDtoToDomain(Event event, CustomExpenseDto customExpenseDto) {
//        BigDecimal cost = customExpenseDto.getCost().isBlank()
//                ? BigDecimal.ZERO
//                : new BigDecimal(customExpenseDto.getCost().replaceAll(",", "."));
//
//        expenseParticipants.forEach(participant -> {
//            List<String> namesOfExistingExpenses = participant.getExpenses().stream()
//                    .map(Expense::getName)
//                    .collect(Collectors.toList());
//            if (!namesOfExistingExpenses.contains(customExpenseDto.getName())) {
//                participant.setBalance(userService.calculateUserBalance(participant.getId()).subtract(costPerParticipant));
//            }
//            userService.save(participant);
//        });
//
//        return Expense.builder()
//                .name(customExpenseDto.getName())
//                .totalCost(cost)
//                .costPerParticipant(costPerParticipant)
//                .event(event)
//                .participants(expenseParticipants)
//                .payoffAmountPerUser(new HashMap<>())
//                .balancePerUser(new HashMap<>())
//                .build();
        return null; //TODO: Pomyśleć jak to chcemy mapować - co brać z frontu i co zapisywać do Expense?
    }

    public static SplitExpenseDto mapToDto(Expense expense) {
        return null;
    }

}
