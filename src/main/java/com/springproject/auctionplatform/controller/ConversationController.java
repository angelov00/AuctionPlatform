package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.config.security.CustomUserDetails;
import com.springproject.auctionplatform.model.DTO.AuctionResponseDTO;
import com.springproject.auctionplatform.model.DTO.ConversationPreviewDTO;
import com.springproject.auctionplatform.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/conversations")
public class ConversationController {
    private final ConversationService conversationService;

    @Autowired
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public String showConversations(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        List<ConversationPreviewDTO> conversations = conversationService.getAllConversationsForUser(user.getUsername());
        model.addAttribute("conversations", conversations);
        model.addAttribute("senderUsername", user.getUsername());

        return "conversations";
    }

    @PostMapping("/{conversationId}/respond")
    public String makeDecision(@PathVariable("conversationId") Long conversationId,
                               @ModelAttribute AuctionResponseDTO responseDTO) {
        conversationService.submitResponse(responseDTO.getSenderUsername(), responseDTO.getResponseStatus(),
            responseDTO.getAuctionId(), conversationId);

        return "redirect:/conversations";
    }
}
