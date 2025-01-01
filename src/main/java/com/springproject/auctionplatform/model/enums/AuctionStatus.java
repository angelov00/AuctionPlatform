package com.springproject.auctionplatform.model.enums;

import lombok.Getter;

@Getter
public enum AuctionStatus {
    UPCOMING("Upcoming"),
    ONGOING("Ongoing"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    WAITING_FOR_FINALIZATION("Waiting for finalization");


    private final String description;

    AuctionStatus(String description) {
        this.description = description;
    }

}
