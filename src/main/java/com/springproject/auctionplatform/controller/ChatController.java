package com.springproject.auctionplatform.controller;

import com.springproject.auctionplatform.model.DTO.AuctionResponseDTO;
import com.springproject.auctionplatform.model.DTO.MessageDTO;
import com.springproject.auctionplatform.model.entity.Message;
import com.springproject.auctionplatform.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationService conversationService;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, ConversationService conversationService) {
        this.messagingTemplate = messagingTemplate;
        this.conversationService = conversationService;
    }

    @MessageMapping("/chat")
    public void send(MessageDTO messageDTO) {
        conversationService.createMessage(messageDTO);

        String recipientUsername = conversationService.getRecipientUsername(messageDTO.getSender(),
            messageDTO.getConversationId());
        String userURI = recipientUsername + "-" + messageDTO.getConversationId();

        messagingTemplate.convertAndSendToUser(userURI, "/queue/messages", messageDTO);
    }

    @GetMapping("/messages/{senderUsername}/{auctionId}/{conversationId}/{completed}")
    public String getChatMessages(@PathVariable("senderUsername") String senderUsername,
                                  @PathVariable("auctionId") long auctionId,
                                  @PathVariable("conversationId") long conversationId,
                                  @PathVariable("completed") String completed,
                                  Model model) {
        List<Message> messages = conversationService.getAllMessagesForConversation(conversationId);

        conversationService.validateUser(senderUsername, conversationId);

        model.addAttribute("messages", messages);
        model.addAttribute("sender", senderUsername);
        model.addAttribute("conversationId", conversationId);
        model.addAttribute("completed", completed.equals("true"));
        model.addAttribute("responseDTO", new AuctionResponseDTO(senderUsername, auctionId));

        return "chat";
    }
}
