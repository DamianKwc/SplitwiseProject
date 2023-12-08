package com.splitwiseapp.controller;

import com.splitwiseapp.entity.Event;
import com.splitwiseapp.entity.User;
import com.splitwiseapp.repository.EventRepository;
import com.splitwiseapp.repository.UserRepository;
import com.splitwiseapp.service.events.EventService;
import com.splitwiseapp.service.users.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class UserMenuController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EventService eventService;
    private final EventRepository eventRepository;

    @GetMapping("/profile")
    public String userProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User loggedInUser = userRepository.findByUsername(username);

        List<Event> userEvents = loggedInUser.getUserEvents();

        model.addAttribute("userEvents", userEvents);
        model.addAttribute("user", loggedInUser);
        return "profile";
    }

    @GetMapping("/users")
    public String users(Model model){
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users";
    }
}
