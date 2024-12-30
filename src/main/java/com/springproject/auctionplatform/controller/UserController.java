package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.security.CustomUserDetails;
import com.springproject.auctionplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public String getUserProfile(@PathVariable("username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);

        return "user-profile";
    }

    @GetMapping("/profile")
    public String getAuthenticatedUserProfile(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        model.addAttribute("user", user);

        return "user-profile";
    }
}
