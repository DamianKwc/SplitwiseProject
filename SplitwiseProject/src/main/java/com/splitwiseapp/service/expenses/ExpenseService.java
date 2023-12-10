package com.splitwiseapp.service.expenses;

import com.splitwiseapp.entity.Expense;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface ExpenseService {
    Expense findById(Integer expenseId);
    List<Expense> findExpensesForGivenEvent(Integer eventId);
    void saveExpense(Expense expense);
    BigDecimal splitCostEquallyPerParticipants(BigDecimal amount, long participantsNumber);
}
