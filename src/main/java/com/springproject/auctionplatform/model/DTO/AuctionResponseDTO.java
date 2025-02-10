package com.springproject.auctionplatform.model.DTO;

import com.springproject.auctionplatform.model.enums.ResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AuctionResponseDTO {

    private String senderUsername;

    private Long auctionId;

    private ResponseStatus responseStatus;

    public AuctionResponseDTO(String senderUsername, Long auctionId) {
        this.senderUsername = senderUsername;
        this.auctionId = auctionId;
    }
}
