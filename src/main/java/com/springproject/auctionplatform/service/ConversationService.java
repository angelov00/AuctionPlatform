package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.model.DTO.MessageDTO;
import com.springproject.auctionplatform.model.entity.Conversation;
import com.springproject.auctionplatform.model.entity.Message;

import java.util.List;
import java.util.Map;

public interface ConversationService {
    Conversation getConversationById(long conversationId);
    List<Conversation> getAllConversationsForUser(String username);
    Conversation createConversation(Conversation conversation);
    void createMessage(MessageDTO messageDTO);
    List<Message> getAllMessagesForConversation(long conversationId);
    void validateUser(String senderUsername, long conversationId);
    String getRecipientUsername(String senderUsername, long conversationId);
    Map<Long, List<String>> getParticipantsByConversationId(String username);
}
