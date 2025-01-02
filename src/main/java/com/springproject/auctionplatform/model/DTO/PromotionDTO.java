package com.springproject.auctionplatform.model.DTO;

import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {

    private Long id;

    private BigDecimal amount;

    private LocalDateTime promotionDate;

    private int duration;

    private PaymentMethod paymentMethod;

    private AuctionPreviewDTO auction;

    private UserDetailsDTO user;
}
