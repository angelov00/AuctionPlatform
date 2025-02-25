package com.springproject.auctionplatform.model.DTO;

import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.validation.EndTimeConstraint;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
public class AuctionAddDTO {

    @NotBlank(message = "Title cannot be blank!")
    @Size(min = 5, max = 100)
    private String title;

    @NotNull(message = "Please provide end time of the auction")
    @Future(message = "Please provide future date")
    @EndTimeConstraint(message = "The auction should be end in no more than 30 days.")
    private LocalDateTime endTime;

    @NotBlank(message = "Enter a description!")
    @Size(min = 10, max = 1000)
    private String description;

    @NotNull(message = "Please select a category")
    private AuctionCategory category;

    @NotNull
    @Size(min = 1, message = "Please upload at least one image!")
    private List<MultipartFile> images;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal startingPrice;

    private Boolean promoteAuction;

}


