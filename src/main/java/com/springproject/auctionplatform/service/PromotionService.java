package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.model.DTO.AuctionPreviewDTO;
import com.springproject.auctionplatform.model.DTO.PromotionDTO;
import com.springproject.auctionplatform.model.DTO.UserDetailsDTO;
import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.repository.PromotionRepository;
import com.springproject.auctionplatform.repository.UserRepository;
import com.springproject.auctionplatform.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    @Autowired
    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public List<PromotionDTO> getPromotionsByUser(User user) {
        return this.promotionRepository.findByUser(user).stream().map(ModelMapper::convertPromotionToPromotionDTO).toList();
    }

    public PromotionDTO getPromotionById(Long promotionId) {
        Optional<Promotion> promotion = this.promotionRepository.findById(promotionId);

        if(promotion.isPresent()) {
            return ModelMapper.convertPromotionToPromotionDTO(promotion.get());
        } else throw new IllegalArgumentException("Promotion not found");

    }

}
