package com.springproject.auctionplatform.model.DTO;

import com.springproject.auctionplatform.model.enums.AuctionCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuctionFilterDTO {

    private AuctionCategory auctionCategory;
    private BigDecimal currentPriceGreaterThan;
    private BigDecimal currentPriceLessThan;
    private String titleSearch;

}
