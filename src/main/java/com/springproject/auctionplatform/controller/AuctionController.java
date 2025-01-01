package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.config.security.CustomUserDetails;
import com.springproject.auctionplatform.model.DTO.AuctionAddDTO;
import com.springproject.auctionplatform.model.DTO.AuctionFilterDTO;
import com.springproject.auctionplatform.model.DTO.AuctionPreviewDTO;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.Bid;
import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
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

    @GetMapping
    public String getAuctions(
            @ModelAttribute AuctionFilterDTO filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size,
            Model model) {

        // TODO
        // If there is no specified category - show user preferred categories

        final int promotedSize = 3;

        // TODO
        // Add promoted auctions pages logic

        Page<AuctionPreviewDTO> pagedPromotedAuctions = auctionService.getPromotedAuctions(0, promotedSize);
        Page<AuctionPreviewDTO> pagedRegularAuctions = auctionService.getRegularAuctions(filter, page, size);

        model.addAttribute("promotedAuctions", pagedPromotedAuctions);
        model.addAttribute("regularAuctions", pagedRegularAuctions);
        model.addAttribute("categories", AuctionCategory.values());
        model.addAttribute("filter", filter);
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
                                BindingResult bindingResult, Model model,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            return "add-auction";
        }

        try {

            Auction auction = auctionService.createAuction(auctionAddDTO, userDetails.getUsername());

            System.out.println("Promotion? " + auctionAddDTO.getPromoteAuction());
            if (!auctionAddDTO.getPromoteAuction()) {
                return "redirect:/home";
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
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            auctionService.placeBid(auctionId, amount, userDetails.getUsername());
            return "redirect:/auction/details/" + auctionId;
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auction/details/" + auctionId;
        }
    }

    @GetMapping("/my-auctions")
    public String myAuctions(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails.getUsername();

        List<Auction> ongoingAuctions = auctionService.getAuctionsBySellerAndStatus(username, AuctionStatus.ONGOING);
        List<Auction> toBeFinalizedAuctions = auctionService.getAuctionsBySellerAndStatus(username, AuctionStatus.WAITING_FOR_FINALIZATION);
        List<Auction> completedAuctions = auctionService.getAuctionsBySellerAndStatus(username, AuctionStatus.COMPLETED);

        model.addAttribute("ongoingAuctions", ongoingAuctions);
        model.addAttribute("toBeFinalizedAuctions", toBeFinalizedAuctions);
        model.addAttribute("completedAuctions", completedAuctions);

        return "my-auctions";
    }

    @GetMapping("/promote/{auctionId}")
    public String promoteAuction(@PathVariable Long auctionId, Model model) {

        Optional<Auction> auction = auctionService.findById(auctionId);
        if (auction.isPresent()) {
            model.addAttribute("auction", auction.get());
        } else {
            return "redirect:/auction";
        }
        return "promote-auction";
    }


    @PostMapping("/promote/{id}")
    public String promoteAuction(@PathVariable Long id,
                                 @RequestParam("paymentMethod") PaymentMethod paymentMethod,
                                 @RequestParam("promotionDuration") int promotionDuration,
                                 Model model) {
        try {
            Promotion promotion = auctionService.promoteAuction(id, paymentMethod, promotionDuration);

            if(promotion != null) return "redirect:/auction/promotion-success/" + promotion.getId();
            else return "redirect:/home";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/home";
        }
    }

    @GetMapping("/promotion-success/{promotionId}")
    public String showPromotionSuccess(@PathVariable Long promotionId, Model model) {
        Promotion promotion = promotionRepository.getPromotionById(promotionId);

        model.addAttribute("promotion", promotion);
        return "promotion-success";
    }



}
