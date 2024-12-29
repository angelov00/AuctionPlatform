package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/conversations")
public class ConversationController {
    private ConversationService conversationService;

    @Autowired
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/{username}")
    public String showConversation(@PathVariable("username") String username, Model model) {
        // TODO validate user session

        model.addAttribute("participantsByConversationId",
            conversationService.getParticipantsByConversationId(username));

        return "conversations";
    }
}
