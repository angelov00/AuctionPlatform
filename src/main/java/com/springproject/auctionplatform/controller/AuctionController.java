package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.config.security.CustomUserDetails;
import com.springproject.auctionplatform.model.DTO.AuctionAddDTO;
import com.springproject.auctionplatform.model.DTO.AuctionFilterDTO;
import com.springproject.auctionplatform.model.DTO.AuctionPreviewDTO;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.Bid;
import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.model.enums.PaymentMethod;
import com.springproject.auctionplatform.repository.PromotionRepository;
import com.springproject.auctionplatform.service.impl.AuctionServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionServiceImpl auctionService;
    private final PromotionRepository promotionRepository;

    @Autowired
    public AuctionController(AuctionServiceImpl auctionService, PromotionRepository promotionRepository) {
        this.auctionService = auctionService;
        this.promotionRepository = promotionRepository;
    }

    @GetMapping()
    public String getAuctions(
            @ModelAttribute AuctionFilterDTO filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {

        //TODO
        //if(filter.getAuctionCategory() == null) filter.setAuctionCategory();


        Page<AuctionPreviewDTO> pagedPromotedAuctions = auctionService.getPromotedAuctions(page, size);
        Page<AuctionPreviewDTO> pagedRegularAuctions = auctionService.getRegularAuctions(filter, page, size);

        model.addAttribute("promotedAuctions", pagedPromotedAuctions);
        model.addAttribute("regularAuctions", pagedRegularAuctions);
        model.addAttribute("categories", AuctionCategory.values());
        model.addAttribute("filter", filter);
        return "browse-auctions";
    }

//    @GetMapping()
//    public String browseAuctions(
//            @RequestParam(required = false) String category,
//            @RequestParam(required = false) BigDecimal minPrice,
//            @RequestParam(required = false) BigDecimal maxPrice,
//            Model model) {
//
//        List<Auction> auctions = auctionService.findAuctions(category, minPrice, maxPrice);
//        model.addAttribute("auctions", auctions);
//        model.addAttribute("categories", AuctionCategory.values());
//        return "browse-auctions";
//    }

    @GetMapping("/create")
    public String createAuction(Model model) {
        model.addAttribute("auctionAddDTO", new AuctionAddDTO());
        model.addAttribute("categories", AuctionCategory.values());
        return "add-auction";
    }

    @PostMapping("/create")
    public String createAuction(@Valid @ModelAttribute AuctionAddDTO auctionAddDTO,
                                BindingResult bindingResult, Model model,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            return "add-auction";
        }

        try {

            Auction auction = auctionService.createAuction(auctionAddDTO, userDetails.getUsername());

            if (auctionAddDTO.getPromoteAuction() != null && auctionAddDTO.getPromoteAuction()) {
                return "redirect:/auction/promote/" + auction.getId();
            }

            return "redirect:/auction/promote/" + auction.getId();
        } catch (IOException e) {
            model.addAttribute("error", e.getMessage());
            return "add-auction";
        }
    }

    @GetMapping("/details/{id}")
    public String auctionDetails(@PathVariable Long id, Model model) {
        Auction auction = auctionService.getAuctionById(id);
        model.addAttribute("auction", auction);
        List<Bid> bids = auctionService.getBidsForAuction(auction.getId());
        bids.sort((b1, b2) -> b2.getTime().compareTo(b1.getTime()));

        model.addAttribute("bids", bids);
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

    @GetMapping("/my-auctions")
    public String myAuctions(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Auction> myAuctions = auctionService.getAuctionsBySeller(userDetails.getUsername());
        model.addAttribute("myAuctions", myAuctions);
        return "my-auctions";
    }

    //TODO Security
    @GetMapping("/promote/{auctionId}")
    public String promoteAuction(@PathVariable Long auctionId, Model model) {
        // Извличаме информацията за търга по ID
        Optional<Auction> auction = auctionService.findById(auctionId);
        if (auction.isPresent()) {
            model.addAttribute("auction", auction.get());
        } else {
            // Ако не е намерен търг, показваме грешка или пренасочваме към друга страница
            return "redirect:/auction";
        }
        return "promote-auction";  // Този шаблон ще показва възможността за промоция
    }

//    @PostMapping("/promote/{auctionId}")
//    public String promoteAuction(@PathVariable Long auctionId, @RequestParam int promotionDuration, Model model) {
//        Optional<Auction> auctionOpt = auctionService.findById(auctionId);
//
////        if (auctionOpt.isPresent()) {
////            Auction auction = auctionOpt.get();
////
////            // Логика за плащането (тук може да имитираш плащане или да интегрираш истинско плащане)
////            // За сега, ще приемем, че плащането е успешно
////
////            // Актуализираме информацията за промоцията
////            auction.setPromotionPrice(promotionPrice);
////            auction.setPromotionEndTime(LocalDateTime.now().plusDays(promotionDuration));
////
////            // Записваме търга с промоцията в базата данни
////            auctionRepository.save(auction);
////
////            // Пренасочваме към страницата с успешна промоция
////            model.addAttribute("auction", auction);
////            return "redirect:/auction/promotions"; // Може да пренасочиш към друга страница за успешна промоция
////        }
//
//
//        this.auctionService.promoteAuction(auctionId, promotionDuration);
//
//
//        return "redirect:/home"; // Ако търгът не е намерен, пренасочваме към главната страница
//    }

    @PostMapping("/promote/{id}")
    public String promoteAuction(@PathVariable Long id,
                                 @RequestParam("paymentMethod") PaymentMethod paymentMethod,
                                 @RequestParam("promotionDuration") int promotionDuration,
                                 Model model) {
        try {
            // Process the promotion (e.g., save promotion details in the database, etc.)
            Promotion promotion = auctionService.promoteAuction(id, paymentMethod, promotionDuration);

            // Redirect to the success page after processing the promotion
            if(promotion != null) return "redirect:/auction/promotion-success/" + promotion.getId();
            else return "redirect:/home";

        } catch (Exception e) {
            // Handle error and show the error page if promotion failed
            model.addAttribute("error", e.getMessage());
            return "redirect:/home";
        }
    }

    // Promotion success page
    @GetMapping("/promotion-success/{promotionId}")
    public String showPromotionSuccess(@PathVariable Long promotionId, Model model) {
        // Retrieve the Promotion object by its ID
        Promotion promotion = promotionRepository.getPromotionById(promotionId);

        // Add the Promotion object to the model to display its details in the view
        model.addAttribute("promotion", promotion);
        return "promotion-success";  // This will render the promotion-success.html template
    }



}
