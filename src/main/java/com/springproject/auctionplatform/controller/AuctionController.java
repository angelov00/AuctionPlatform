package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.model.DTO.AuctionAddDTO;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.service.impl.AuctionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionServiceImpl auctionService;

    @Autowired
    public AuctionController(AuctionServiceImpl auctionService) {
        this.auctionService = auctionService;
    }

    @GetMapping("/create")
    public String createAuction(Model model) {
        return "add-auction";
    }

    public String createAuction(@Valid @ModelAttribute AuctionAddDTO auctionAddDTO,
                                BindingResult bindingResult, Model model,
                                @AuthenticationPrincipal User currentUser) {

        if (bindingResult.hasErrors()) {
            return "auction/create";  // Връща формата, ако има грешки
        }
        try {
            Auction auction = auctionService.createAuction(auctionAddDTO, currentUser);
            return "redirect:/auctions";  // Пренасочва към списъка с аукциони
        } catch (IOException e) {
            model.addAttribute("error", "Failed to upload images.");
            return "auction/create";  // Връща форма с грешка при качване на изображения
        }
    }
}
