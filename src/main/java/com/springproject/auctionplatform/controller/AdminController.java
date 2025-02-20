package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.Role;
import com.springproject.auctionplatform.service.AuctionService;
import com.springproject.auctionplatform.service.BidService;
import com.springproject.auctionplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final AuctionService auctionService;
    private final BidService bidService;

    @Autowired
    public AdminController(UserService userService, AuctionService auctionService, BidService bidService) {
        this.userService = userService;
        this.auctionService = auctionService;
        this.bidService = bidService;
    }

    @GetMapping
    public String index(Model model) {
        return "admin-dashboard";
    }

    @GetMapping("/stats")
    public String stats(Model model) {

        long totalUsers = this.userService.countUsers();
        long bannedUsers = this.userService.countBannedUser();
        long activeAuctions = this.auctionService.countActiveAuctions();
        long totalBids = this.bidService.getTotalBidsCount();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("bannedUsers", bannedUsers);
        model.addAttribute("activeAuctions", activeAuctions);
        model.addAttribute("totalBids", totalBids);
        return "admin-statistics";
    }

    @GetMapping("/users")
    public String users(Model model, @RequestParam(name = "search", required = false) String searchTerm) {

        List<User> users = this.userService.findAllList().stream().filter(u -> !u.getRoles().contains(Role.ROLE_ADMIN)).toList();

        //TODO add repo method to search by name
        if(searchTerm != null && !searchTerm.isEmpty()) {
            users = users.stream().filter(u -> u.getUsername().toLowerCase().contains(searchTerm.toLowerCase())).toList();
        }

        model.addAttribute("search", searchTerm);
        model.addAttribute("users", users);
        return "user-management";
    }

    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Long id) {
        this.userService.banUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Long id) {
        this.userService.unbanUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/promote-to-admin")
    public String promoteToAdmin(@PathVariable Long id) {
        this.userService.promoteToAdmin(id);
        return "redirect:/admin/users";
    }
}
