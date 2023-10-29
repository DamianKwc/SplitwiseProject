package com.splitwiseapp.service.events;

import com.splitwiseapp.dto.events.EventDto;
import com.splitwiseapp.entity.EventEntity;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.UserRepository;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.splitwiseapp.shared.UserUtils.getCurrentlyLoggedInUser;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<EventDto> findAllEvents() {
        List<EventEntity> events = eventRepository.findAll();
        return events.stream()
                .map(this::mapToEventsDto)
                .collect(Collectors.toList());
    }

    @Override
    public void saveEvent(EventDto eventDto) {
        EventEntity event = new EventEntity();
        event.setEventName(eventDto.getEventName());
        event.setOwner(getCurrentlyLoggedInUser(userRepository));

        eventRepository.save(event);
    }

    private EventDto mapToEventsDto(EventEntity event) {
        EventDto eventDto = new EventDto();
        eventDto.setEventName(event.getEventName());

        return eventDto;
    }


    @Override
    public EventEntity findByEventName(@NotEmpty String eventName) {
        return eventRepository.findByEventName(eventName);
    }

}