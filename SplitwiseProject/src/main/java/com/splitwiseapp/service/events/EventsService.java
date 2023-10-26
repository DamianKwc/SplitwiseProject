package com.splitwiseapp.service.events;

import com.splitwiseapp.dto.eventsDto.EventsDto;
import com.splitwiseapp.entity.EventsEntity;
import com.splitwiseapp.entity.UserEntity;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface EventsService {
    List<EventsDto> findAllEvents();

    void saveEvent(EventsDto eventsDto);

    EventsEntity findByEventName(@NotEmpty String eventName);
}
