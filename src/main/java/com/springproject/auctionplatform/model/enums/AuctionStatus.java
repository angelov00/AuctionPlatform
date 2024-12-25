package com.springproject.auctionplatform.model.enums;

import lombok.Getter;

@Getter
public enum AuctionStatus {
    UPCOMING("Upcoming"),
    ONGOING("Ongoing"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");


    private final String description;

    AuctionStatus(String description) {
        this.description = description;
    }

}
