package com.splitwiseapp.repository;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {
    Event findByEventNameAndOwner(String eventName, User owner);
    List<Event> findEventsByOwnerOrEventMembers(User user, User member);
}