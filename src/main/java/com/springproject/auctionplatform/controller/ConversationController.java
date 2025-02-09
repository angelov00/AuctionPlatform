package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.config.security.CustomUserDetails;
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

@Controller
@RequestMapping("/conversations")
public class ConversationController {
    private final ConversationService conversationService;

    @Autowired
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping
    public String showConversation(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        model.addAttribute("participantsByConversationId",
            conversationService.getParticipantsByConversationId(user.getUsername()));

        return "conversations";
    }

    @PostMapping
    public String addConversation(@Valid Conversation conversation,
                                  @AuthenticationPrincipal CustomUserDetails user) {
        if (!conversation.getParticipants().contains(user.getUser())) {
            throw new RuntimeException("Participant not in conversation");
        }

        conversationService.createConversation(conversation);

        return "redirect:/conversations";
    }
}
