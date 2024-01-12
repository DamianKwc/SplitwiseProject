package com.splitwiseapp.repository;

import com.splitwiseapp.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    Expense findByName(String expenseName);
    Expense findByNameAndEventId(String expenseName, Integer eventId);
}