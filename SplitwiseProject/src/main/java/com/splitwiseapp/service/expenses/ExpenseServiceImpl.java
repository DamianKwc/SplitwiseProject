package com.splitwiseapp.service.expenses;

import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private ExpenseRepository expenseRepository;
    private EventRepository eventRepository;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    //expenses without duplicates
    @Override
    public List<Expense> viewExpenses() {
        List<Expense> allExpenses = expenseRepository.findAll();
        Set<String> uniqueExpenseNames = new HashSet<>();

        List<Expense> uniqueExpenses = allExpenses.stream()
                .filter(expense -> uniqueExpenseNames.add(expense.getExpenseName()))
                .collect(Collectors.toList());

        return uniqueExpenses;
    }
    //expenses without duplicates and withing event by id
    @Override
    public List<Expense> viewExpensesByEventId(Integer eventId) {
        List<Expense> allExpenses = expenseRepository.findByEventId(eventId);

        Set<String> uniqueExpenseNames = new HashSet<>();
        List<Expense> uniqueExpenses = new ArrayList<>();

        for (Expense expense : allExpenses) {
            if (uniqueExpenseNames.add(expense.getExpenseName())) {
                uniqueExpenses.add(expense);
            }
        }

        return uniqueExpenses;
    }

    @Override
    public List<Expense> findAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public void saveExpense(Expense expense) {
        expenseRepository.save(expense);
    }

    @Override
    public Expense findById(Integer expenseId) {
        return expenseRepository.findById(expenseId).orElseThrow();
    }

    @Override
    public Expense findByExpenseName(String expenseName) {
        return expenseRepository.findByExpenseName(expenseName);
    }

    @Override
    public void deleteById(Integer expenseId) {
        expenseRepository.deleteById(expenseId);
    }
}
