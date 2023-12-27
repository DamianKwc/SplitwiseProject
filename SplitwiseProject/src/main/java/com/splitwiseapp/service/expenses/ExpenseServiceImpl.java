package com.splitwiseapp.service.expenses;

import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.Payoff;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.ExpenseRepository;
import com.splitwiseapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


@Data
@Service
@AllArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private ExpenseRepository expenseRepository;
    private EventRepository eventRepository;
    private UserRepository userRepository;

    @Override
    public Expense findById(Integer expenseId) {
        return expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + expenseId));
    }

    @Override
    public List<Expense> findExpensesForGivenEvent(Integer eventId) {
        return expenseRepository.findAll().stream()
                .filter(expense -> eventId.equals(expense.getEvent().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void saveExpense(Expense expense) {
        expenseRepository.save(expense);
    }

    @Override
    public BigDecimal splitCostEquallyPerParticipants(BigDecimal amount, long participantsNumber) {
        return amount.divide(BigDecimal.valueOf(participantsNumber), 2, RoundingMode.CEILING);
    }

    @Override
    public void deleteById(Integer expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    @Override
    public Map<Integer, Map<Integer, BigDecimal>> mapExpenseToUserPayoffAmount(List<Expense> eventExpenses) {
        Map<Integer, Map<Integer, BigDecimal>> mapExpensePerUserAndPayoffAmount = new HashMap<>();
        Map<Integer, BigDecimal> mapPayoffsAmountPerUser = new HashMap<>();

        eventExpenses.forEach(expense -> {
                expense.getParticipants().forEach(participant -> mapPayoffsAmountPerUser.put(participant.getId(),
                        expense.getPayoffs().stream()
                                .filter(payoff -> participant.getId().equals(payoff.getUserPaying().getId()))
                                .filter(payoff -> expense.getId().equals(payoff.getExpensePaid().getId()))
                                .map(Payoff::getPayoffAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                ));

                mapExpensePerUserAndPayoffAmount.put(expense.getId(), mapPayoffsAmountPerUser);
                expense.setExpenseToUserPayoffAmount(mapExpensePerUserAndPayoffAmount);
                expenseRepository.save(expense);
        });

        return mapExpensePerUserAndPayoffAmount;
    }

    private BigDecimal getPayoffsSumForParticipant(List<Payoff> payoffs, Integer expenseId) {
        return payoffs.stream()
                .filter(payoff -> expenseId.equals(payoff.getExpensePaid().getId()))
                .map(Payoff::getPayoffAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
