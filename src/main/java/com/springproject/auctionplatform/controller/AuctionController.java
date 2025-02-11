package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.config.security.CustomUserDetails;
import com.springproject.auctionplatform.model.DTO.*;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.Bid;
import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import com.springproject.auctionplatform.model.enums.PaymentMethod;
import com.springproject.auctionplatform.service.AuctionService;
import com.springproject.auctionplatform.service.PromotionService;
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


@Controller
@RequestMapping("/auction")
public class AuctionController {

    public static final int PROMOTED_PER_PAGE= 3;
    private final AuctionService auctionService;
    private final PromotionService promotionService;

    @Autowired
    public AuctionController(AuctionService auctionService, PromotionService promotionService) {
        this.auctionService = auctionService;
        this.promotionService = promotionService;
    }

    @GetMapping
    public String getAuctions(
            @ModelAttribute AuctionFilterDTO filter,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size,
            Model model) {

        long promotedAuctionsCount = this.auctionService.getPromotedAuctionsCount();

        if(promotedAuctionsCount > 0) {
            int currentPromotionPage = page * PROMOTED_PER_PAGE % (int) this.auctionService.getPromotedAuctionsCount();
            Page<AuctionPreviewDTO> pagedPromotedAuctions = auctionService.getPromotedAuctions(currentPromotionPage, PROMOTED_PER_PAGE);
            model.addAttribute("promotedAuctions", pagedPromotedAuctions);
        }

        Page<AuctionPreviewDTO> pagedRegularAuctions = auctionService.getRegularAuctions(filter, page, size);
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
            model.addAttribute("categories", AuctionCategory.values());
            return "add-auction";
        }

        try {
            Auction auction = auctionService.createAuction(auctionAddDTO, userDetails.getUsername());
            if (auctionAddDTO.getPromoteAuction()) {
                return "redirect:/auction/promote/" + auction.getId();
            }

            return "redirect:/home";
        } catch (IOException e) {
            model.addAttribute("error", e.getMessage());
            return "add-auction";
        }
    }

    @GetMapping("/details/{id}")
    public String auctionDetails(@PathVariable Long id, Model model) {

        AuctionDetailsDTO auction = this.auctionService.getDetailsById(id);
        model.addAttribute("auction", auction);

        List<BidDetailsDTO> bids = auctionService.getBidsForAuction(auction.getId());
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
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auction/details/" + auctionId;
        }
    }

    @GetMapping("/my-auctions")
    public String myAuctions(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String username = userDetails.getUsername();

        List<AuctionPreviewDTO> ongoingAuctions = auctionService.getAuctionsBySellerAndStatus(username, AuctionStatus.ONGOING);
        List<AuctionPreviewDTO> toBeFinalizedAuctions = auctionService.getAuctionsBySellerAndStatus(username, AuctionStatus.WAITING_FOR_FINALIZATION);
        List<AuctionPreviewDTO> completedAuctions = auctionService.getAuctionsBySellerAndStatus(username, AuctionStatus.COMPLETED);

        model.addAttribute("ongoingAuctions", ongoingAuctions);
        model.addAttribute("toBeFinalizedAuctions", toBeFinalizedAuctions);
        model.addAttribute("completedAuctions", completedAuctions);

        return "my-auctions";
    }

    @GetMapping("/promote/{auctionId}")
    public String promoteAuction(@PathVariable Long auctionId, Model model) {

        try {
        AuctionDetailsDTO auction = this.auctionService.getDetailsById(auctionId);
        model.addAttribute("auction", auction);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
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

        PromotionDTO promotion = this.promotionService.getPromotionById(promotionId);
        model.addAttribute("promotion", promotion);
        return "promotion-success";
    }





}
