package com.splitwiseapp.service.expenses;

import com.splitwiseapp.dto.expense.CustomExpenseDto;
import com.splitwiseapp.dto.expense.ExpenseMapper;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface ExpenseService {
    void save(Expense expense);
    void deleteById(Integer expenseId);
    void deleteByEventId(Integer eventId);
    Expense findById(Integer expenseId);
    List<Expense> findExpensesForGivenEvent(Integer eventId);
    BigDecimal splitCostEquallyPerParticipants(BigDecimal amount, long participantsNumber);
    Map<Integer, BigDecimal> mapUserToCost(Expense expense);
    Map<Integer, BigDecimal> mapUserToPayoffAmount(Expense expense);
    Map<Integer, BigDecimal> mapUserToBalance(Expense expense);
    Expense findByExpenseNameAndEventId(String expenseName, Integer eventId);
    void updateExpenseAttributes(List<Expense> eventExpenses);
    BigDecimal calculateUpdatedBalanceForEvent(List<Expense> eventExpenses);
    Expense createExpense(Event foundEvent, User loggedInUser, CustomExpenseDto customExpenseDto, ExpenseMapper expenseMapper);
    void updateParticipantsAndDeleteExpense(Expense expense, Map<Integer, BigDecimal> costPerParticipant, Map<Integer, BigDecimal> payoffPerParticipant);
}
