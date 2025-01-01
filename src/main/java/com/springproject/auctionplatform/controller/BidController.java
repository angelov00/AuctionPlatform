package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.config.security.CustomUserDetails;
import com.springproject.auctionplatform.model.entity.Bid;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.service.BidService;
import com.springproject.auctionplatform.service.impl.BidServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/bids")
public class BidController {

    private final BidServiceImpl bidService;

    @Autowired
    public BidController(BidServiceImpl bidService) {
        this.bidService = bidService;
    }

    @GetMapping("/history")
    public String viewBidHistory(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        List<Bid> userBids = this.bidService.findBidsByUser(user);
        model.addAttribute("bids", userBids);
        return "bid-history";
    }
}
