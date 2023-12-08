package com.splitwiseapp.service.expenses;

import com.splitwiseapp.entity.Expense;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ExpenseService {
    Expense findById(Integer expenseId);
    List<Expense> findAll();
    List<Expense> findExpensesForGivenEvent(Integer eventId);
    void saveExpense(Expense expense);
}
