package com.splitwiseapp.service.expenses;

import com.splitwiseapp.dto.expenses.ExpenseDto;
import com.splitwiseapp.dto.users.UserDto;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.User;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public interface ExpenseService {
    List<Expense> getAllExpenses();

    List<Expense> getExpensesByEvent(Event event);

    List<Expense> getExpensesByUser(User user);

    Expense getExpenseById(Integer expenseId);

//    Expense createExpense(ExpenseDto expenseDto);

    void deleteExpense(Integer expenseId);

    void saveExpense(ExpenseDto expenseDto);

    Expense save(Expense expense);

    void saveExpense(Expense expense);
}
