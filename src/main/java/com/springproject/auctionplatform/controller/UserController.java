package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.model.DTO.AuctionPreviewDTO;
import com.springproject.auctionplatform.model.DTO.UserUpdateDTO;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.config.security.CustomUserDetails;
import com.springproject.auctionplatform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails.getUser());
        return "user-profile";
    }

    @GetMapping("/edit")
    public String editProfile(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName(user.getFirstName());
        userUpdateDTO.setLastName(user.getLastName());
        userUpdateDTO.setEmail(user.getEmail());
        userUpdateDTO.setPhone(user.getPhone());

        model.addAttribute("userUpdateDTO", userUpdateDTO);

        return "edit-user-profile";
    }

    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @Valid @ModelAttribute UserUpdateDTO updatedUser,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "edit-user-profile"; // Връщане на формата с грешки
        }

        User user = userDetails.getUser();
        userService.updateUserProfile(user, updatedUser);

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
        return "redirect:/users/profile";
    }


    @GetMapping
    public String getAuthenticatedUserProfile(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        model.addAttribute("user", user);

        return "user-profile";
    }

    @GetMapping("/watchlist")
    public String getWatchList(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userDetails.getUser();
        List<AuctionPreviewDTO> watchlist = userService.getWatchlist(user);
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
        return "redirect:/users/watchlist";
    }


}
