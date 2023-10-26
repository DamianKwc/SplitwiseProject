package com.splitwiseapp.repository;

import com.splitwiseapp.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity,Integer> {

    RoleEntity findByRole(String role);
}
