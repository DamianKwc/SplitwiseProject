package com.splitwiseapp.repository;

import com.splitwiseapp.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository <UserEntity, Integer> {

    UserEntity findByUsername(String username);

    Optional<UserEntity> findById(Integer userId);

}
