package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.config.security.CustomUserDetails;
import com.springproject.auctionplatform.service.UserService;
import com.springproject.auctionplatform.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
       // User user = userService.getUserByUsername(username);
       // model.addAttribute("user", user);

        if(userDetails == null) return "redirect:/auth/login";

        model.addAttribute("user", userDetails.getUser());
        return "user-profile";
    }

    @GetMapping("/edit")
    public String editProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        if (user == null) {
            return "redirect:/auth/login"; // Пренасочване, ако потребителят не е логнат
        }
        model.addAttribute("user", user);
        return "edit-user-profile"; // Връща изглед за редактиране на профила
    }

    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @ModelAttribute User updatedUser, Model model,
                                RedirectAttributes redirectAttributes) {

        User user = userDetails.getUser();
        if (user == null) {
            return "redirect:/auth/login"; // Пренасочване, ако потребителят не е логнат
        }

        userService.updateUserProfile(user, updatedUser);

        //model.addAttribute("message", "Profile updated successfully!");
        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
        return "redirect:/users/profile"; // Пренасочва обратно към страницата с профила
    }

    @GetMapping
    public String getAuthenticatedUserProfile(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        model.addAttribute("user", user);

        return "user-profile";
    }

    @GetMapping("/watchlist")
    public String getWatchList(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        Set<Auction> watchlist = userService.getWatchlist(user);
        model.addAttribute("watchlist", watchlist);
        return "watchlist";
    }

    @PostMapping("/watchlist/add/{auctionId}")
    public String addToWatchlist(@PathVariable Long auctionId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        userService.addToWatchlist(user, auctionId);
        return "redirect:/users/watchlist";
    }

    @PostMapping("/watchlist/remove/{auctionId}")
    public String removeFromWatchlist(@PathVariable Long auctionId, @AuthenticationPrincipal CustomUserDetails userDetails, RedirectAttributes redirectAttributes) {
        User user = userDetails.getUser();
        Auction auction = userService.removeFromWatchlist(user, auctionId);
        redirectAttributes.addFlashAttribute("message", "You have removed auction '" + auction.getTitle() + "' from your watchlist.");
        return "redirect:/users/watchlist"; // Пренасочване към страницата с наблюдавани търгове
    }


}
