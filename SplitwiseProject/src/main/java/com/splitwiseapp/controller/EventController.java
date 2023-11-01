package com.splitwiseapp.controller;

import com.splitwiseapp.dto.events.EventDto;
import com.splitwiseapp.dto.users.UserDto;
import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.UserRepository;
import com.splitwiseapp.service.events.EventService;
import com.splitwiseapp.service.users.UserService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Set;

import static com.splitwiseapp.shared.UserUtils.getCurrentlyLoggedInUser;

@Controller
@AllArgsConstructor
public class EventController {

    //zrobić dodawanie użytkowników
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/newEvent")
    public String showEventAddingForm(Model model){
        List<UserDto> allUsers = userService.findAllUsers();
        model.addAttribute("newEvent", new EventDto());
        model.addAttribute("allUsers", allUsers);
        return "new-event";
    }

    @GetMapping("/events")
    public String events(Model model) {
        List<Event> events = eventService.findAllEvents();
        model.addAttribute("events", events);
        return "events";
    }

    @PostMapping("/saveEvent")
    public String addEvent(@ModelAttribute("events") EventDto eventDto,
                           BindingResult result,
                           Model model) {

        if (doesEventWithGivenNameAlreadyExist(eventDto)) {
            result.rejectValue("eventName", null,
                    "That event already exists or given name is incorrect.");
        }
        if (result.hasErrors()) {
            model.addAttribute("events", eventDto);
            return "redirect:/events";
        }

        Event event = Event.builder()
                .eventName(eventDto.getEventName())
                .owner(getCurrentlyLoggedInUser(userRepository))
                .build();

        event.enrollUser(event.getOwner());
        eventService.saveEvent(event);
        return "redirect:/events";
    }

    @GetMapping("/event/edit/{id}")
    public String editEvent(@PathVariable Integer id, Model model) {
        Event eventToBeEdited = eventService.findById(id);
        if (eventToBeEdited != null) {
            model.addAttribute("newEvent", eventToBeEdited);
        }
        return "new-event";
    }

    @GetMapping("/{eventId}/users/{userId}")
    public String enrollUserToEvent(@PathVariable Integer eventId, @PathVariable Integer userId, Model model) {
        Event event = eventService.findById(eventId);
        Set<User> eventUsers = event.getEventUsers();
        User user = userService.findById(userId);

        if (event != null && user != null) {
            model.addAttribute("newEvent", event);
            model.addAttribute("eventUsers", eventUsers);
            event.enrollUser(user);
            eventRepository.save(event);
        }
        return "new-event";
    }

    private boolean doesEventWithGivenNameAlreadyExist(EventDto eventDto) {
        Event event = eventService.findByEventName(eventDto.getEventName());
        return event != null && !StringUtils.isBlank(event.getEventName());
    }

}