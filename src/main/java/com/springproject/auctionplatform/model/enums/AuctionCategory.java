package com.springproject.auctionplatform.model.enums;

public enum AuctionCategory {
    ELECTRONICS("Electronics and Gadgets"),
    FURNITURE("Furniture and Home Decor"),
    COLLECTIBLES("Antiques and Collectibles"),
    VEHICLES("Vehicles and Automobiles"),
    FASHION("Clothing, Shoes, and Accessories"),
    REAL_ESTATE("Real Estate and Properties"),
    ART("Art and Paintings"),
    SPORTS("Sports and Outdoor Equipment"),
    BOOKS_AND_MEDIA("Books, Media, and Entertainment"),
    JEWELRY("Jewelry and Watches"),
    TOYS("Toys and Games"),
    OTHERS("Other Categories");

    private final String displayName;

    AuctionCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
