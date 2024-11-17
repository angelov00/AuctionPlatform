package com.springproject.auctionplatform.model.DTO;

import com.springproject.auctionplatform.model.enums.AuctionCategory;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
public class AuctionAddDTO {

    @NotBlank
    @Size(min = 5, max = 100)
    private String title;

    @FutureOrPresent
    @NotNull
    private LocalDateTime endTime;

    @NotBlank
    @Size(min = 10, max = 1000)
    private String description;

    @NotNull
    private AuctionCategory category;

    @NotNull
    @Size(min = 1)
    private List<@NotNull @Size(max = 255) String> imageURLs;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal startingPrice;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal reservePrice;

}


