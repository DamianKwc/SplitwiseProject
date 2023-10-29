package com.splitwiseapp.repository;

import com.splitwiseapp.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, Integer> {
    EventEntity findByEventName(String eventName);
}