package com.splitwiseapp.service.events;

import com.splitwiseapp.dto.eventsDto.EventsDto;
import com.splitwiseapp.entity.EventsEntity;

import com.splitwiseapp.entity.UserEntity;
import com.splitwiseapp.repository.EventsRepository;
import com.splitwiseapp.repository.UserRepository;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventsServiceImpl implements EventsService {

    private EventsRepository eventsRepository;
    private UserRepository userRepository;

    @Autowired
    public EventsServiceImpl(EventsRepository eventsRepository, UserRepository userRepository) {
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<EventsDto> findAllEvents() {
        List<EventsEntity> events = eventsRepository.findAll();
        return events.stream()
                .map((event) -> mapToEventsDto(event))
                .collect(Collectors.toList());
    }

    @Override
    public void saveEvent(EventsDto eventsDto) {
        EventsEntity event = new EventsEntity();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity loggedInUser = userRepository.findByUsername(username);

        event.setEventName(eventsDto.getEventName());
        event.setOwner(loggedInUser);

        eventsRepository.save(event);
    }

    private EventsDto mapToEventsDto(EventsEntity event){
        EventsDto eventsDto = new EventsDto();
        eventsDto.setEventName(event.getEventName());

        return eventsDto;
    }

    @Override
    public EventsEntity findByEventName(@NotEmpty String eventName) {
        return  eventsRepository.findByEventName(eventName);
    }
    }



