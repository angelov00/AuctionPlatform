package com.springproject.auctionplatform.model.DTO;

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
public class BidDetailsDTO {

    private Long id;

    private BigDecimal amount;

    private LocalDateTime time;

    private UserDetailsDTO bidder;

    private AuctionDetailsDTO auction;
}
