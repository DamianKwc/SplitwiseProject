package com.splitwiseapp.controller;

import com.splitwiseapp.entity.UserEntity;
import com.splitwiseapp.repository.UserRepository;
import com.splitwiseapp.service.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserMenuController {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserMenuController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }
    @GetMapping("/profile")
    public String userProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserEntity loggedInUser = userRepository.findByUsername(username);
        model.addAttribute("user", loggedInUser);
        return "profile";
    }

}
