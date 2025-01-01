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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public String getUserProfile(@PathVariable("username") String username, Model model) {
        User user = userService.getUserByUsername(username);
        model.addAttribute("user", user);

        return "user-profile";
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
    public String removeFromWatchlist(@PathVariable Long auctionId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        userService.removeFromWatchlist(user, auctionId);
        return "redirect:/users/watchlist"; // Пренасочване към страницата с наблюдавани търгове
    }


}
