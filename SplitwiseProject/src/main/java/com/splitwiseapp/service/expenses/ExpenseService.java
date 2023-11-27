package com.splitwiseapp.service.expenses;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public interface ExpenseService {
    List<Expense> findAllExpenses();
    void saveExpense(Expense expense);
    Expense findById(@NotEmpty Integer expenseId);
    Expense findByExpenseName(@NotEmpty String expenseName);
    void deleteById(Integer expenseId);

    List<Expense> viewExpensesByEventId(Integer eventId);

    default List<Expense> viewExpenses() {
        List<Expense> allExpenses = findAllExpenses();
        Set<String> uniqueExpenseNames = new HashSet<>();

        List<Expense> uniqueExpenses = allExpenses.stream()
                .filter(expense -> uniqueExpenseNames.add(expense.getExpenseName()))
                .collect(Collectors.toList());

        return uniqueExpenses;
    }
}
