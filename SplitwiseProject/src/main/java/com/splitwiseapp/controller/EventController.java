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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.splitwiseapp.shared.UserUtils.getCurrentlyLoggedInUser;

@Controller
@AllArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/events")
    public String events(Model model) {
        List<Event> events = eventService.findAllEvents();
        model.addAttribute("events", events);
        return "events";
    }

    @GetMapping("/newEvent")
    public String showEventAddingForm(Model model){
        List<UserDto> allUsers = userService.findAllUsers();
        model.addAttribute("newEvent", new EventDto());
        model.addAttribute("allUsers", allUsers);
        return "new-event";
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

        event.addUser(getCurrentlyLoggedInUser(userRepository));
        event.setOwner(getCurrentlyLoggedInUser(userRepository));
        eventService.saveEvent(event);

        return "redirect:/events";
    }

    @GetMapping("/delete")
    public String deleteEvent(@RequestParam("eventId") Integer eventId) {
        eventService.deleteById(eventId);
        return "redirect:/events";
    }

   @GetMapping("/events/{id}/users")
   public String viewUsers(@PathVariable("id") Integer id, Model model) {
       Event event = eventService.findById(id);
       List<User> users = event.getEventUsers();

       if(users.isEmpty()) {
           return "redirect:/events/" + id + "/addUsers";
       }

       model.addAttribute("remove_id", id);
       model.addAttribute("users", users);
       return "users";

   }

   @GetMapping("/events/{id}/addUsers")
   public String addUsers(@PathVariable("id") Integer id, Model model) {
       List<User> eventUsers = eventService.findById(id).getEventUsers();
       List<User> users = userService.findAll();
       List<User> remainingUsers = new ArrayList<User>();
       for (User u: users)
       {
           if (!eventUsers.contains(u)) {
               remainingUsers.add(u);
           }
       }
       model.addAttribute("users",remainingUsers);
       model.addAttribute("add_id", id);
       return "users";
   }

    @GetMapping("/events/{eid}/addUser")
    public String addUser(@PathVariable("eid") Integer eid, @RequestParam("uid") Integer uid) {
        Event event = eventService.findById(eid);
        User user = userService.findById(uid);
        event.addUser(user);
        eventService.save(event);
        user.addEvent(event);
        userService.save(user);
        return "redirect:/events/" + eid + "/users";
    }

    @GetMapping("/events/{eid}/removeUser")
    public String removeUser(@PathVariable("eid") Integer eid, @RequestParam("uid") Integer uid) {
        Event event = eventService.findById(eid);
        User user = userService.findById(uid);
        event.removeUser(user);
        eventService.save(event);
        user.removeEvent(event);
        userService.save(user);
        return "redirect:/events/" + eid + "/users";
    }

    private boolean doesEventWithGivenNameAlreadyExist(EventDto eventDto) {
        Event event = eventService.findByEventName(eventDto.getEventName());
        return event != null && !StringUtils.isBlank(event.getEventName());
    }

}