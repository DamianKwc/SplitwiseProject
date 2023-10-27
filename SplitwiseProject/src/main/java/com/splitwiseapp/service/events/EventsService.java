package com.splitwiseapp.service.events;

import com.splitwiseapp.dto.events.EventDto;
import com.splitwiseapp.entity.EventsEntity;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface EventsService {
    List<EventDto> findAllEvents();

    void saveEvent(EventDto eventDto);

    EventsEntity findByEventName(@NotEmpty String eventName);
}
