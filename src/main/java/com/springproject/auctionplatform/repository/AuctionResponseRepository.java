package com.springproject.auctionplatform.repository;

import com.springproject.auctionplatform.model.entity.AuctionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuctionResponseRepository extends JpaRepository<AuctionResponse, Long> {
    Optional<AuctionResponse> findByConversationIdAndUserUsername(Long conversationId, String username);
    List<AuctionResponse> findByConversationId(Long conversationId);
}
