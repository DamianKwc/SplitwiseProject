package com.splitwiseapp.service.users;

import com.splitwiseapp.dto.expense.CustomExpenseDto;
import com.splitwiseapp.dto.expense.SplitExpenseDto;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.Expense;
import com.splitwiseapp.entity.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public interface UserService {
    void save(User user);
    User findById(Integer userId);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    List<User> getUsersByNames(SplitExpenseDto splitExpenseDto);
    List<User> getUsersByNames(CustomExpenseDto customExpenseDto);
    Map<Event, BigDecimal> balanceInEachEvent(User user, List<Event> events, Set<Expense> expenses);
}
