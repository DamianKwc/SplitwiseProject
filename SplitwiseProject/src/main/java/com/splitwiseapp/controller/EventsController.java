package com.splitwiseapp.controller;

import com.splitwiseapp.dto.events.EventDto;
import com.splitwiseapp.entity.EventsEntity;
import com.splitwiseapp.entity.UserEntity;
import com.splitwiseapp.repository.EventsRepository;
import com.splitwiseapp.repository.UserRepository;
import com.splitwiseapp.service.events.EventsService;
import com.splitwiseapp.service.users.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class EventsController {

    //zrobić dodawanie użytkowników
    private final EventsService eventsService;
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/addEvent")
    public String showEventAddingForm(Model model){
        EventDto events = new EventDto();
        model.addAttribute("events", events);
        return "addEvent";
    }

    @GetMapping("/events")
    public String events(Model model) {
        List<EventDto> events = eventsService.findAllEvents();
        model.addAttribute("events", events);
        return "events";
    }

    @PostMapping("/addEvent")
    public String addEvent(@ModelAttribute("events") EventDto eventDto,
                           BindingResult result,
                           Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        UserEntity loggedInUser = userRepository.findByUsername(currentUsername);

        EventsEntity eventEntity = new EventsEntity();
        eventEntity.setEventName(eventDto.getEventName());
        eventEntity.setOwner(loggedInUser);

        EventsEntity existingEvent = eventsService.findByEventName(eventDto.getEventName());

        if (existingEvent != null && existingEvent.getEventName() != null && existingEvent.getEventName().isEmpty()) {
            result.rejectValue("eventName", null,
                    "Event with that name already exists");
        }
        if (result.hasErrors()) {
            model.addAttribute("events", eventDto);
            return "events";
        }
        eventsService.saveEvent(eventDto);
        return "events";
    }
}