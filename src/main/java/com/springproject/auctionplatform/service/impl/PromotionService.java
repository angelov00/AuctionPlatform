package com.springproject.auctionplatform.service.impl;

import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.repository.PromotionRepository;
import com.springproject.auctionplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final UserRepository userRepository;

    @Autowired
    public PromotionService(PromotionRepository promotionRepository, UserRepository userRepository) {
        this.promotionRepository = promotionRepository;
        this.userRepository = userRepository;
    }

    public List<Promotion> getPromotionsByUser(User user) {
        return this.promotionRepository.findByUser(user);

    }

    public Promotion getPromotionById(Long promotionId) {
        Optional<Promotion> promotion = this.promotionRepository.findById(promotionId);
        if(promotion.isEmpty()) {
            throw new IllegalArgumentException("No promotion found with id " + promotionId);
        }
        return this.promotionRepository.findById(promotionId).get();
    }
}
