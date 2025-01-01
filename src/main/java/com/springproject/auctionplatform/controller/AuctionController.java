package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.config.security.CustomUserDetails;
import com.springproject.auctionplatform.model.DTO.AuctionAddDTO;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.Bid;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.service.impl.AuctionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionServiceImpl auctionService;

    @Autowired
    public AuctionController(AuctionServiceImpl auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping()
    public String browseAuctions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model) {

        List<Auction> auctions = auctionService.findAuctions(category, minPrice, maxPrice);
        model.addAttribute("auctions", auctions);
        model.addAttribute("categories", AuctionCategory.values());
        return "browse-auctions";
    }

    @GetMapping("/create")
    public String createAuction(Model model) {
        model.addAttribute("auctionAddDTO", new AuctionAddDTO());
        model.addAttribute("categories", AuctionCategory.values());
        return "add-auction";
    }

    @PostMapping("/create")
    public String createAuction(@Valid @ModelAttribute AuctionAddDTO auctionAddDTO,
                                BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            return "add-auction";  // Връща формата, ако има грешки
        }

        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            CustomUserDetails userDetails = (CustomUserDetails) principal;

            Auction auction = auctionService.createAuction(auctionAddDTO, userDetails.getUsername());
            return "redirect:/home";  // Пренасочва към списъка с аукциони
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload images.");
            return "add-auction";  // Връща форма с грешка при качване на изображения
        }
    }

    @GetMapping("/details/{id}")
    public String auctionDetails(@PathVariable Long id, Model model) {
        Auction auction = auctionService.getAuctionById(id);
        model.addAttribute("auction", auction);
        model.addAttribute("bids", auctionService.getBidsForAuction(id));
        model.addAttribute("newBid", new Bid());
        return "auction-details";
    }

    @PostMapping("/bid")
    public String placeBid(@RequestParam Long auctionId,
                           @RequestParam BigDecimal amount,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        try {
            auctionService.placeBid(auctionId, amount, userDetails.getUsername());
            return "redirect:/auction/details/" + auctionId;
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/auction/details/" + auctionId;
        }
    }




}
