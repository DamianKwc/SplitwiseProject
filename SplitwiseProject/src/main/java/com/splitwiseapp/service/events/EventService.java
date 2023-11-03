package com.splitwiseapp.service.events;

import com.splitwiseapp.entity.Event;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {

    List<Event> findAllEvents();
    void saveEvent(Event event);
    Event findById(@NotEmpty Integer eventId);
    Event findByEventName(@NotEmpty String eventName);

    void deleteById(Integer eventId);
}
