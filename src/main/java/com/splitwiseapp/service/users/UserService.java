package com.splitwiseapp.service.users;

import com.splitwiseapp.entity.UserEntity;
import com.splitwiseapp.dto.userDto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService{

    void saveUser(UserDto userDto);
    List<UserDto> findAllUsers();

    UserEntity findByUsername(String username);

    UserEntity getLoggedInUser();

    Optional<UserEntity> findById(Integer userId);

}
