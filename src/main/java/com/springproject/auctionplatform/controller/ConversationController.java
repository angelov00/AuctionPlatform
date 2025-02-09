package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.config.security.CustomUserDetails;
import com.springproject.auctionplatform.model.DTO.ConversationPreviewDTO;
import com.springproject.auctionplatform.model.entity.Conversation;
import com.springproject.auctionplatform.service.ConversationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
        conversations.forEach(c -> System.out.println(c.getConversationTitle()));
        model.addAttribute("conversations", conversations);
        model.addAttribute("senderUsername", user.getUsername());

        return "conversations";
    }

//    @PostMapping
//    public String addConversation(@Valid @ModelAttribute("conversationDTO") ConversationDTO conversationDTO,
//                                  @AuthenticationPrincipal CustomUserDetails user) {
//        System.out.println(conversationDTO);
//        System.out.println(user.getUser().getId());
//        if (!conversationDTO.getSellerId().equals(user.getUser().getId())) {
//            throw new RuntimeException("Participant not in conversation");
//        }
//
//        conversationService.createConversation(conversationDTO.getSellerId(), conversationDTO.getBuyerId());
//
//        return "redirect:/conversations";
//    }
}
