package com.splitwiseapp.service.events;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public void save(Event event) {
        eventRepository.save(event);
    }

    @Override
    public Event findById(Integer eventId) {
        return eventRepository.findById(eventId).orElseThrow();
    }

    @Override
    public Optional<Event> findByEventNameAndOwner(String eventName, User owner) {
        return Optional.ofNullable(eventRepository.findByEventNameAndOwner(eventName, owner));
    }
    @Override
    public void deleteById(Integer eventId) {
        eventRepository.deleteById(eventId);
    }

    @Override
    public List<Event> findEventsByUser(User user) {
        return eventRepository.findEventsByOwnerOrEventMembers(user, user);
    }
}