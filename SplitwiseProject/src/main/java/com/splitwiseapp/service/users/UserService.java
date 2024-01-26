package com.splitwiseapp.service.users;

import com.splitwiseapp.dto.expense.CustomExpenseDto;
import com.splitwiseapp.dto.expense.SplitExpenseDto;
import com.splitwiseapp.entity.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Service
public interface UserService {

    void save(User user);
    List<User> findAllUsers();
    User findById(Integer userId);
    User findByUsername(String username);
    List<User> findAll();
    User getCurrentlyLoggedInUser();
    BigDecimal calculateUserBalance(Integer userId);
    List<User> getUsersByNames(SplitExpenseDto splitExpenseDto);
    List<User> getUsersByNames(CustomExpenseDto customExpenseDto);
}
