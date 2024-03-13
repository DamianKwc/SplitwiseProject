package com.splitwiseapp.dto.event;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.service.users.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@AllArgsConstructor
public class EventMapper {

    private final UserService userService;

    public Event mapToDomain(EventDto eventDto) {
        return Event.builder()
                .eventName(getEventName(eventDto))
                .creationDate(LocalDate.now())
                .owner(userService.getCurrentlyLoggedInUser())
                .creationDate(LocalDate.now())
                .build();
    }

    private static String getEventName(EventDto eventDto) {
        return Character.toUpperCase(eventDto.getEventName().charAt(0)) + eventDto.getEventName().substring(1);
    }
}
