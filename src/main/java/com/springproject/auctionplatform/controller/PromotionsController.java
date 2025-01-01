package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.config.security.CustomUserDetails;
import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.service.impl.PromotionService;
import com.springproject.auctionplatform.util.PDFGenerator;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

        if(user == null) return "redirect:/auth/login";
        List<Promotion> promotionList = this.promotionService.getPromotionsByUser(user);
        model.addAttribute("promotions", promotionList);
        return "my-promotions";
    }

    @GetMapping("/generateInvoice/{promotionId}")
    public void generateInvoice(@PathVariable Long promotionId, HttpServletResponse response) throws Exception {
        // Получаваме промоцията по ID
        Promotion promotion = promotionService.getPromotionById(promotionId);

        if (promotion != null) {
            // Настройваме типа на отговора за PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=promotion_invoice.pdf");

            // Генерираме PDF файла
            pdfGenerator.generateInvoice(promotion, response);
        } else {
            // В случай, че няма промоция с такова ID, можем да покажем съобщение за грешка
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Promotion not found");
        }
    }
}
