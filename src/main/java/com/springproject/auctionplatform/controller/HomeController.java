package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.config.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    //Пренасочва заявката към основната страница ("/home")
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String showHomePage(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {

        if(userDetails != null) {
            String fullName = userDetails.getFirstName() + " " + userDetails.getLastName();
            model.addAttribute("fullName", fullName);
        }

        return "home";
    }
}
