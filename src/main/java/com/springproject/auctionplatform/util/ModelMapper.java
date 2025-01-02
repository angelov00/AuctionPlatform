package com.springproject.auctionplatform.util;

import com.springproject.auctionplatform.model.DTO.*;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.Bid;
import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.entity.User;

public class ModelMapper {

    public static AuctionPreviewDTO convertAuctionToAuctionPreviewDTO(Auction auction) {
        return new AuctionPreviewDTO(
                auction.getId(),
                auction.getTitle(),
                auction.getEndTime(),
                auction.getDescription(),
                auction.getCategory(),
                auction.getImageURLs().getFirst(),
                auction.getCurrentPrice(),
                auction.getStartingPrice()
        );
    }

    public static AuctionDetailsDTO convertAuctionToAuctionDetailsDTO(Auction auction) {
        AuctionDetailsDTO auctionDetailsDTO = new AuctionDetailsDTO();
        auctionDetailsDTO.setId(auction.getId());
        auctionDetailsDTO.setTitle(auction.getTitle());
        auctionDetailsDTO.setStartTime(auction.getStartTime());
        auctionDetailsDTO.setEndTime(auction.getEndTime());
        auctionDetailsDTO.setDescription(auction.getDescription());
        auctionDetailsDTO.setStatus(auction.getStatus());
        auctionDetailsDTO.setCategory(auction.getCategory());

        auction.getImageURLs().forEach(imageURL -> auctionDetailsDTO.getImageURLs().add(imageURL));
        auctionDetailsDTO.setStartingPrice(auction.getStartingPrice());
        auctionDetailsDTO.setCurrentPrice(auction.getCurrentPrice());

        UserDetailsDTO seller = new UserDetailsDTO();
        seller.setId(auction.getSeller().getId());
        seller.setUsername(auction.getSeller().getUsername());
        seller.setFirstName(auction.getSeller().getFirstName());
        seller.setLastName(auction.getSeller().getLastName());
        seller.setEmail(auction.getSeller().getEmail());

        auctionDetailsDTO.setSeller(seller);
        auctionDetailsDTO.setPromoted(auction.isPromoted());
        auctionDetailsDTO.setPromotedAt(auction.getPromotedAt());
        auctionDetailsDTO.setPromotionEndTime(auction.getPromotionEndTime());
        //auctionDetailsDTO.setBuyer(auction.getBuyer());

        return auctionDetailsDTO;
    }

    public static PromotionDTO convertPromotionToPromotionDTO(Promotion promotion) {
        PromotionDTO promotionDTO = new PromotionDTO();
        promotionDTO.setId(promotion.getId());
        promotionDTO.setAmount(promotion.getAmount());
        promotionDTO.setDuration(promotion.getDuration());
        promotionDTO.setPaymentMethod(promotion.getPaymentMethod());
        promotionDTO.setPromotionDate(promotion.getPromotionDate());

        UserDetailsDTO user = new UserDetailsDTO();
        user.setId(promotion.getUser().getId());
        user.setUsername(promotion.getUser().getUsername());
        user.setFirstName(promotion.getUser().getFirstName());
        user.setLastName(promotion.getUser().getLastName());
        user.setEmail(promotion.getUser().getEmail());
        user.setPhone(promotion.getUser().getPhone());
        promotionDTO.setUser(user);

        AuctionPreviewDTO auction = new AuctionPreviewDTO();
        auction.setId(promotion.getAuction().getId());
        auction.setTitle(promotion.getAuction().getTitle());
        auction.setDescription(promotion.getAuction().getDescription());
        auction.setCategory(promotion.getAuction().getCategory());
        auction.setEndTime(promotion.getAuction().getEndTime());
        auction.setStartingPrice(promotion.getAuction().getStartingPrice());
        promotionDTO.setAuction(auction);

        return promotionDTO;
    }

    public static BidDetailsDTO convertBidToBidDetailsDTO(Bid bid) {
        BidDetailsDTO bidDetailsDTO = new BidDetailsDTO();
        bidDetailsDTO.setId(bid.getId());
        bidDetailsDTO.setTime(bid.getTime());
        bidDetailsDTO.setAmount(bid.getAmount());
        bidDetailsDTO.setAuction(ModelMapper.convertAuctionToAuctionDetailsDTO(bid.getAuction()));
        UserDetailsDTO bidder = new UserDetailsDTO();
        bidder.setId(bid.getUser().getId());
        bidder.setUsername(bid.getUser().getUsername());
        bidder.setFirstName(bid.getUser().getFirstName());
        bidder.setLastName(bid.getUser().getLastName());
        bidDetailsDTO.setBidder(bidder);

        return bidDetailsDTO;
    }

    public static UserDetailsDTO convertUserToUserDetailsDTO(User user) {
        return null;
    }


}
