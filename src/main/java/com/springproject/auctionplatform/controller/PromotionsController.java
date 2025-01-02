package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.config.security.CustomUserDetails;
import com.springproject.auctionplatform.model.DTO.PromotionDTO;
import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.service.PromotionService;
import com.springproject.auctionplatform.util.PDFGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/promotions")
public class PromotionsController {

    private final PromotionService promotionService;

    private final PDFGenerator pdfGenerator;

    @Autowired
    public PromotionsController(PromotionService promotionService, PDFGenerator pdfGenerator) {
        this.promotionService = promotionService;
        this.pdfGenerator = pdfGenerator;
    }

    @GetMapping("")
    public String getPromotions(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        User user = userDetails.getUser();
        List<PromotionDTO> promotionList = this.promotionService.getPromotionsByUser(user);
        model.addAttribute("promotions", promotionList);
        return "my-promotions";
    }

    @GetMapping("/generateInvoice/{promotionId}")
    public void generateInvoice(@PathVariable Long promotionId, HttpServletResponse response) throws Exception {

        PromotionDTO promotion = promotionService.getPromotionById(promotionId);

        if (promotion != null) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=promotion_invoice.pdf");

            pdfGenerator.generateInvoice(promotion, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Promotion not found");
        }
    }
}
