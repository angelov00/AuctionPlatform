package com.springproject.auctionplatform.model.enums;

public enum PromotionLevel {

    BASIC("Basic Promotion", 10.00, "24 часа ."),
    STANDARD("Standard Promotion", 25.00, "48 часа."),
    PREMIUM("Premium Promotion", 50.00, "72 часа.");

    private final String description;
    private final double price;
    private final String details;

    PromotionLevel(String description, double price, String details) {
        this.description = description;
        this.price = price;
        this.details = details;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return description + " - лв." + price;
    }

}
