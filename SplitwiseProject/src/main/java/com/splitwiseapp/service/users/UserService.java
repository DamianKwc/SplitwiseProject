package com.splitwiseapp.service.users;

import com.splitwiseapp.entity.User;
import com.splitwiseapp.dto.users.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService{

    void saveUser(UserDto userDto);
    List<UserDto> findAllUsers();
    User findById(Integer userId);
    User findByUsername(String username);

}
