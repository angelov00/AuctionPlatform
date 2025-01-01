package com.springproject.auctionplatform.repository;

import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Promotion getPromotionById(Long promotionId);

    List<Promotion> findByUser(User user);
}
