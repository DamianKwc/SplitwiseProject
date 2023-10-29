package com.splitwiseapp.controller;

import com.splitwiseapp.dto.events.EventDto;
import com.splitwiseapp.entity.EventEntity;
import com.splitwiseapp.service.events.EventService;
import com.splitwiseapp.service.members.MemberService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class EventController {

    //zrobić dodawanie użytkowników
    private final EventService eventService;
    private final MemberService memberService;

    @GetMapping("/newEvent")
    public String showEventAddingForm(Model model){
        EventDto events = new EventDto();
        model.addAttribute("events", events);
        return "addEvent";
    }

    @GetMapping("/events")
    public String events(Model model) {
        List<EventDto> events = eventService.findAllEvents();
        model.addAttribute("events", events);
        return "events";
    }

    @PostMapping("/saveEvent")
    public String addEvent(@ModelAttribute("events") EventDto eventDto,
                           BindingResult result,
                           Model model) {
        EventEntity existingEvent = eventService.findByEventName(eventDto.getEventName());

        if (existingEvent != null && !StringUtils.isBlank(existingEvent.getEventName())) {
            result.rejectValue("eventName", null,
                    "Event with that name already exists");
        }
        if (result.hasErrors()) {
            model.addAttribute("events", eventDto);
            return "events";
        }
        eventService.saveEvent(eventDto);
        memberService.saveEventMember(eventDto);
        return "events";
    }
}