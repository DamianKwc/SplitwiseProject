package com.splitwiseapp.service.events;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.User;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {
    void save(Event event);
    void deleteById(Integer eventId);
    List<Event> findAllEvents();
    Event findById(@NotEmpty Integer eventId);
    Event findByEventNameAndOwner(String eventName, User owner);
    List<Event> findEventsByUser(User user);
}

