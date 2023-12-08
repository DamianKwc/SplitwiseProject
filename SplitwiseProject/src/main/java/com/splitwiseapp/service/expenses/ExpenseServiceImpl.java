package com.splitwiseapp.service.expenses;

import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.ExpenseRepository;
import com.splitwiseapp.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public List<Expense> findAll() {
        return expenseRepository.findAll();
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

}
