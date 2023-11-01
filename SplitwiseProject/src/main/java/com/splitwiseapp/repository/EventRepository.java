package com.splitwiseapp.repository;

import com.splitwiseapp.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Event findByEventName(String eventName);
}