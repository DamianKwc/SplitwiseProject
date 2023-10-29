package com.splitwiseapp.service.events;

import com.splitwiseapp.dto.events.EventDto;
import com.splitwiseapp.entity.EventEntity;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EventService {

    List<EventDto> findAllEvents();
    void saveEvent(EventDto eventDto);
    EventEntity findByEventName(@NotEmpty String eventName);
}
