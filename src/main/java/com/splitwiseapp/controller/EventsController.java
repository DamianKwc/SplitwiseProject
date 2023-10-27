package com.splitwiseapp.controller;

import com.splitwiseapp.dto.eventsDto.EventsDto;
import com.splitwiseapp.entity.EventsEntity;
import com.splitwiseapp.entity.UserEntity;
import com.splitwiseapp.repository.EventsRepository;
import com.splitwiseapp.repository.UserRepository;
import com.splitwiseapp.service.events.EventsService;
import com.splitwiseapp.service.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class EventsController {

    //zrobić dodawanie użytkowników
    private final EventsService eventsService;
    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public EventsController(EventsService eventsService, EventsRepository eventsRepository, UserRepository userRepository, UserService userService) {
        this.eventsService = eventsService;
        this.eventsRepository = eventsRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/addEvent")
    public String showEventAddingForm(Model model){

        EventsDto events = new EventsDto();
        model.addAttribute("events", events);

        return "addEvent";
    }

    @GetMapping("/events")
    public String events(Model model) {
        List<EventsDto> events = eventsService.findAllEvents();
        model.addAttribute("events", events);
        return "events";
    }

    @PostMapping("/addEvent")
    public String addEvent(@ModelAttribute("events") EventsDto eventsDto,
                           BindingResult result,
                           Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        UserEntity loggedInUser = userRepository.findByUsername(currentUsername);

        EventsEntity eventEntity = new EventsEntity();
        eventEntity.setEventName(eventsDto.getEventName());
        eventEntity.setOwner(loggedInUser);


        EventsEntity existingEvent = eventsService.findByEventName(eventsDto.getEventName());
        if (existingEvent != null) {
            result.rejectValue("eventName", null, "Event with that name already exists");
        }

        if (result.hasErrors()) {
            model.addAttribute("events", eventsDto);
            return "events";
        }
        eventsService.saveEvent(eventsDto);
        return "events";
    }
}
