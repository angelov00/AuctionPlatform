package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.exception.ResourceNotFoundException;
import com.springproject.auctionplatform.model.DTO.PromotionDTO;
import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.repository.PromotionRepository;
import com.springproject.auctionplatform.util.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        return this.promotionRepository.findByUser(user).stream().sorted((p1, p2) -> p2.getPromotionDate().compareTo(p1.getPromotionDate())).map(ModelMapper::convertPromotionToPromotionDTO).toList();
    }

    public PromotionDTO getPromotionById(Long promotionId) {
        Optional<Promotion> promotion = this.promotionRepository.findById(promotionId);

        if(promotion.isPresent()) {
            return ModelMapper.convertPromotionToPromotionDTO(promotion.get());
        } else throw new ResourceNotFoundException("Promotion not found");

    }

}
