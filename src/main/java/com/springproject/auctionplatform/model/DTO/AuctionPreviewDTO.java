package com.springproject.auctionplatform.model.DTO;

import com.springproject.auctionplatform.model.enums.AuctionCategory;
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
public class AuctionPreviewDTO {

    private Long id;

    private String title;

    private LocalDateTime endTime;

    private String description;

    private AuctionCategory category;

    private String mainPhotoURL;

    private BigDecimal currentPrice;

    private Long sellerId;

    private Long buyerId;

    private BigDecimal startingPrice;
}
