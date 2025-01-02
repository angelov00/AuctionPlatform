package com.springproject.auctionplatform.model.DTO;

import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionDetailsDTO {

    private Long id;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;

    private AuctionStatus status;

    private AuctionCategory category;

    private List<String> imageURLs = new ArrayList<>();

    private BigDecimal startingPrice;

    private BigDecimal currentPrice;

    private UserDetailsDTO seller;

    private boolean isPromoted;

    private LocalDateTime promotedAt;

    private LocalDateTime promotionEndTime = null;

    private UserDetailsDTO buyer;
}
