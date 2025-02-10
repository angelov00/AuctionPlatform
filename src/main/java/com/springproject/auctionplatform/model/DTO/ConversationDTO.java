package com.springproject.auctionplatform.model.DTO;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ConversationDTO {
    private Long sellerId;
    private Long buyerId;
}
