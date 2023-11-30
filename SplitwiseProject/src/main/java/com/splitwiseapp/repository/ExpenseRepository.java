package com.splitwiseapp.repository;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    Expense findByExpenseName(String expenseName);
    List<Expense> findByEventId(Integer eventId);

    List<Expense> findByEvent(Event event);

    List<Expense> findByUser(User user);

    List<Expense> findByEventAndUser(Event event, User user);


}