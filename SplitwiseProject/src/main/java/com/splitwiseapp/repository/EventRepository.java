package com.splitwiseapp.repository;

import com.splitwiseapp.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Event findByEventName(String eventName);
    List<Event> findByEventNameContainingIgnoreCase(String eventName);
}