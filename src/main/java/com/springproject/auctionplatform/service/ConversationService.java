package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.model.DTO.MessageDTO;
import com.springproject.auctionplatform.model.entity.Conversation;
import com.springproject.auctionplatform.model.entity.Message;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.repository.ConversationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserService userService;

    @Autowired
    public ConversationService(ConversationRepository conversationRepository, UserService userService) {
        this.conversationRepository = conversationRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public Conversation getConversationById(long conversationId) {
        return conversationRepository.findById(conversationId)
            .orElseThrow(() -> new EntityNotFoundException("Conversation with given id not found"));
    }

    @Transactional(readOnly = true)
    public List<Conversation> getAllConversationsForUser(String username) {
        return conversationRepository.findByUsername(username);
    }

    public Conversation createConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public void createMessage(MessageDTO messageDTO) {
        Conversation conversation = getConversationById(messageDTO.getConversationId());
        Message message = new Message(messageDTO.getContent(), userService.getUserByUsername(messageDTO.getSender()));

        conversation.addMessage(message);

        conversationRepository.save(conversation);
    }

    @Transactional(readOnly = true)
    public List<Message> getAllMessagesForConversation(long conversationId) {
        Conversation conversation = getConversationById(conversationId);

        return conversation.getMessages();
    }

    @Transactional(readOnly = true)
    public void validateUser(String senderUsername, long conversationId) {
        User sender = userService.getUserByUsername(senderUsername);
        Conversation conversation = getConversationById(conversationId);

        if (!sender.getConversations().contains(conversation)) {
            throw new EntityNotFoundException("User does not have a conversation with the given id");
        }
    }

    @Transactional(readOnly = true)
    public String getRecipientUsername(String senderUsername, long conversationId) {
        Conversation conversation = getConversationById(conversationId);

        User recipient = conversation.getParticipants().stream()
            .filter(u -> !u.getUsername().equals(senderUsername))
            .findFirst().orElse(null);

        assert recipient != null;

        return recipient.getUsername();
    }

    @Transactional(readOnly = true)
    public Map<Long, List<String>> getParticipantsByConversationId(String username) {
        List<Conversation> conversations = getAllConversationsForUser(username);

        return conversations.stream()
            .collect(Collectors.toMap(
                Conversation::getId,
                c -> getParticipantsUsernames(c.getId())
            ));
    }

    private List<String> getParticipantsUsernames(long conversationId) {
        Conversation conversation = getConversationById(conversationId);

        return conversation.getParticipants().stream()
            .map(User::getUsername)
            .collect(Collectors.toList());
    }
}
