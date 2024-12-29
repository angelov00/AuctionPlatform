package com.springproject.auctionplatform.repository;

import com.springproject.auctionplatform.model.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c, IN(c.participants) i WHERE i.username = :username")
    List<Conversation> findByUsername(@Param("username") String username);
}
