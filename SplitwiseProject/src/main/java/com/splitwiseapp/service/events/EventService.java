package com.splitwiseapp.service.events;

import com.splitwiseapp.dto.events.EventDto;
import com.splitwiseapp.dto.users.UserDto;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.User;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {

    List<Event> findAllEvents();
    void saveEvent(Event event);
    Event findById(@NotEmpty Integer eventId);
    Event findByEventName(String eventName);
    void deleteById(Integer eventId);
    Event save(Event event);

}
