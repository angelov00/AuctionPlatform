package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.service.AuctionService;
import com.springproject.auctionplatform.service.impl.AuctionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionServiceImpl auctionService;

    public AuctionController(AuctionServiceImpl auctionService) {
        this.auctionService = auctionService;
    }
}
