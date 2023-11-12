package com.splitwiseapp.service.events;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.UserRepository;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;



    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, UserRepository userRepository1) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository1;
    }

    @Override
    public List<Event> findAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public void saveEvent(Event event) {
        eventRepository.save(event);
    }

    @Override
    public Event findById(Integer eventId) {
        return eventRepository.findById(eventId).orElseThrow();
    }

    @Override
    public Event findByEventName(@NotEmpty String eventName) {
        return eventRepository.findByEventName(eventName);
    }

    @Override
    public void deleteById(Integer eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

}