package com.springproject.auctionplatform.model.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MessageDTO {
    private String content;
    private String sender;
    private long conversationId;
}
