package com.splitwiseapp.service.events;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.User;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface EventService {
    void save(Event event);
    void deleteById(Integer eventId);
    Event findById(@NotEmpty Integer eventId);
    Optional<Event> findByEventNameAndOwner(String eventName, User owner);
    List<Event> findEventsByUser(User user);
}

