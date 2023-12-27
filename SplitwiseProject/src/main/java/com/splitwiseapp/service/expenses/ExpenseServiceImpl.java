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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Map<Integer, BigDecimal> mapExpenseToUserPayoffAmount(Expense expense) {
        Map<Integer, BigDecimal> mapPayoffsAmountPerUser = new HashMap<>();

            expense.getParticipants().forEach(participant -> mapPayoffsAmountPerUser.put(participant.getId(),
                    sumParticipantPayoffs(expense, participant)
                ));

        return mapPayoffsAmountPerUser;
    }

    private BigDecimal sumParticipantPayoffs(Expense expense, User participant) {
        return expense.getPayoffs().stream()
                .filter(payoff -> participant.getId().equals(payoff.getUserPaying().getId()))
                .map(Payoff::getPayoffAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
