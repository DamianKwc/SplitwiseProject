package com.splitwiseapp.service.users;

import com.splitwiseapp.dto.expense.CustomExpenseDto;
import com.splitwiseapp.dto.expense.SplitExpenseDto;
import com.splitwiseapp.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    void save(User user);
    List<User> findAllUsers();
    User findById(Integer userId);
    User findByUsername(String username);
    List<User> findAll();
    User getCurrentlyLoggedInUser();
    List<User> getUsersByNames(SplitExpenseDto splitExpenseDto);
    List<User> getUsersByNames(CustomExpenseDto customExpenseDto);
}
