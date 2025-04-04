package com.springproject.auctionplatform.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConversationPreviewDTO {

    private Long conversationId;

    private String conversationTitle;

    private String highestBidderUsername;

    private Long auctionId;

    private boolean completed;

}
