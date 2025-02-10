package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.model.DTO.ConversationPreviewDTO;
import com.springproject.auctionplatform.model.DTO.MessageDTO;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.AuctionResponse;
import com.springproject.auctionplatform.model.entity.Conversation;
import com.springproject.auctionplatform.model.entity.Message;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import com.springproject.auctionplatform.model.enums.ResponseStatus;
import com.springproject.auctionplatform.repository.AuctionRepository;
import com.springproject.auctionplatform.repository.AuctionResponseRepository;
import com.springproject.auctionplatform.repository.ConversationRepository;
import com.springproject.auctionplatform.util.ModelMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserService userService;
    private final AuctionResponseRepository auctionResponseRepository;
    private final AuctionRepository auctionRepository;

    @Autowired
    public ConversationService(ConversationRepository conversationRepository, UserService userService, AuctionResponseRepository auctionResponseRepository, AuctionRepository auctionRepository) {
        this.conversationRepository = conversationRepository;
        this.userService = userService;
        this.auctionResponseRepository = auctionResponseRepository;
        this.auctionRepository = auctionRepository;
    }

    @Transactional(readOnly = true)
    public Conversation getConversationById(long conversationId) {
        return conversationRepository.findById(conversationId)
            .orElseThrow(() -> new EntityNotFoundException("Conversation with given id not found"));
    }

    @Transactional(readOnly = true)
    public List<ConversationPreviewDTO> getAllConversationsForUser(String username) {
        return conversationRepository.findByUsername(username).stream().map(c -> ModelMapper.convertConversationToConversationPreviewDTO(c, username)).toList();
    }

    public Conversation createConversation(User seller, User buyer) {
        Conversation conversation = new Conversation(seller, buyer);

        conversationRepository.save(conversation);

        return conversation;
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

    public void updateConversation(Conversation conversation) {
        conversationRepository.save(conversation);
    }

    public void submitResponse(String senderUsername, ResponseStatus responseStatus, Long auctionId, Long conversationId) {
        Auction auction = auctionRepository.findById(auctionId)
            .orElseThrow(() -> new RuntimeException("Auction not found"));
        User user = userService.getUserByUsername(senderUsername);
        Conversation conversation = getConversationById(conversationId);

        AuctionResponse response = auctionResponseRepository
            .findByConversationIdAndUserUsername(conversationId, senderUsername)
            .orElse(new AuctionResponse());

        response.setConversation(conversation);
        response.setUser(user);
        response.setResponse(responseStatus);
        auctionResponseRepository.save(response);

        List<AuctionResponse> responses = auctionResponseRepository.findByConversationId(conversationId);
        boolean bothAccepted = responses.size() == 2 &&
            responses.stream().allMatch(r -> r.getResponse() == ResponseStatus.ACCEPT);
        boolean someoneDeclined = responses.size() == 2 &&
            responses.stream().anyMatch(r -> r.getResponse() == ResponseStatus.DECLINE);

        if (bothAccepted) {
            auction.setStatus(AuctionStatus.COMPLETED);
            auction.setBuyer(responses.stream().filter(r -> !r.getUser().getId().equals(auction.getSeller().getId())).peek(u -> System.out.println(u.getId())).findFirst().get()
                .getUser());
            auction.setCompletionTime(LocalDateTime.now());
        } else if (someoneDeclined) {
            auction.setStatus(AuctionStatus.CANCELLED);
        }

        auctionRepository.save(auction);
    }

    private List<String> getParticipantsUsernames(long conversationId) {
        Conversation conversation = getConversationById(conversationId);

        return conversation.getParticipants().stream()
            .map(User::getUsername)
            .collect(Collectors.toList());
    }
}
