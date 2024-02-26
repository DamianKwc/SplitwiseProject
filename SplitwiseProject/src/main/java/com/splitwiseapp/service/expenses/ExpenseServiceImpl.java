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
import java.util.Objects;
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
    public void save(Expense expense) {
        expenseRepository.save(expense);
    }

    @Override
    public List<Expense> saveAll(List<Expense> expenses) {
        return expenseRepository.saveAll(expenses);
    }


    @Override
    public BigDecimal splitCostEquallyPerParticipants(BigDecimal amount, long participantsNumber) {
        return participantsNumber == 0
                ? BigDecimal.ZERO
                : amount.divide(BigDecimal.valueOf(participantsNumber), 2, RoundingMode.CEILING);
    }

    @Override
    public void deleteById(Integer expenseId) {
        expenseRepository.deleteById(expenseId);
    }

    @Override
    public Map<Integer, BigDecimal> mapUserToCost(Expense expense) {
        Map<Integer, BigDecimal> mapUserPerCost = new HashMap<>();
        expense.getParticipants().forEach(participant -> mapUserPerCost.put(participant.getId(),
                sumParticipantCosts(expense, participant)
        ));
        return mapUserPerCost;
    }

    @Override
    public Map<Integer, BigDecimal> mapUserToPayoffAmount(Expense expense) {
        Map<Integer, BigDecimal> mapUserPerPayoffsAmount = new HashMap<>();
        expense.getParticipants().forEach(participant -> mapUserPerPayoffsAmount.put(participant.getId(),
                sumParticipantPayoffs(expense, participant)
        ));
        return mapUserPerPayoffsAmount;
    }

    @Override
    public Map<Integer, BigDecimal> mapUserToBalance(Expense expense) {
        Map<Integer, BigDecimal> mapUserPerBalance = new HashMap<>();
        expense.getParticipants().forEach(participant -> mapUserPerBalance.put(participant.getId(),
                calculateParticipantBalance(expense, participant)
        ));
        return mapUserPerBalance;
    }

    @Override
    public void deleteByEventId(Integer eventId) {
        expenseRepository.deleteById(eventId);
    }

    @Override
    public Expense findByExpenseNameAndEventId(String expenseName, Integer eventId) {
        return expenseRepository.findByNameAndEventId(expenseName, eventId);
    }

    private BigDecimal sumParticipantPayoffs(Expense expense, User participant) {
        return expense.getPayoffs().stream()
                .filter(Objects::nonNull)
                .filter(payoff -> participant.getId().equals(payoff.getUserPaying().getId()))
                .map(Payoff::getPayoffAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateParticipantBalance(Expense expense, User participant) {
        BigDecimal payoffsSum = sumParticipantPayoffs(expense, participant);
        BigDecimal totalCost = expense.getTotalCost();
        BigDecimal participantPayoff = payoffsSum;

        if (expense.getParticipants().contains(participant)) {
            participantPayoff = payoffsSum.subtract(expense.getPayoffPerUser().getOrDefault(participant.getId(), BigDecimal.ZERO));
        }

        BigDecimal balance;
        if (expense.getCostPerUser().containsKey(participant.getId())) {
            BigDecimal participantCost = expense.getCostPerUser().get(participant.getId());
            balance = participantPayoff.subtract(participantCost);
        } else {
            balance = participantPayoff.subtract(splitCostEquallyPerParticipants(totalCost, expense.getParticipants().size()));
        }

        return balance;
    }

    private BigDecimal sumParticipantCosts(Expense expense, User participant) {
        return expense.getCostPerUser().entrySet().stream()
                .filter(costMap -> participant.getId().equals(costMap.getKey()))
                .map(Map.Entry::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
