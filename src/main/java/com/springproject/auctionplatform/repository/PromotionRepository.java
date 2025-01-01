package com.springproject.auctionplatform.repository;

import com.springproject.auctionplatform.model.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Promotion getPromotionById(Long promotionId);
}
