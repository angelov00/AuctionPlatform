package com.springproject.auctionplatform.model.DTO;

import com.springproject.auctionplatform.model.enums.AuctionCategory;
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

    @FutureOrPresent(message = "Start time must be in the future or present!")
    @NotNull(message = "Start time is required!")
    private LocalDateTime startTime;

    @Future(message = "End time must be in the future!")
    @NotNull(message = "End time is required!")
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

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal reservePrice;

}


