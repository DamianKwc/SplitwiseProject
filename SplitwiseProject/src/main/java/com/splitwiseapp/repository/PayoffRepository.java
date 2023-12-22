package com.splitwiseapp.repository;

import com.splitwiseapp.entity.Payoff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoffRepository extends JpaRepository<Payoff, Integer> {
}