package com.splitwiseapp.repository;

import com.splitwiseapp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository <UserEntity, Integer> {

    UserEntity findByUsername(String username);

}
