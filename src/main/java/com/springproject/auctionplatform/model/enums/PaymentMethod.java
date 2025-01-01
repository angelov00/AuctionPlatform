package com.springproject.auctionplatform.model.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CREDIT_CARD("Pay using a credit card"),
    DEBIT_CARD("Pay using a debit card"),
    PAYPAL("Pay using PayPal"),
    BANK_TRANSFER("Pay using bank transfer"),
    EASY_PAY("Cash payment option"),
    APPLE_PAY("Pay using Apple Pay"),
    GOOGLE_PAY("Pay using Google Pay");

    // Getter method to access the description
    private final String description;

    // Constructor to set the description for each enum value
    PaymentMethod(String description) {
        this.description = description;
    }

}
