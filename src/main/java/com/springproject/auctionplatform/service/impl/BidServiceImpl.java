package com.springproject.auctionplatform.service.impl;

import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.Bid;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.repository.AuctionRepository;
import com.springproject.auctionplatform.repository.BidRepository;
import com.springproject.auctionplatform.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BidServiceImpl implements BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    @Autowired
    public BidServiceImpl(BidRepository bidRepository, AuctionRepository auctionRepository) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
    }

    Bid placeBid(Long auctionId, BigDecimal bidAmount, User user) {

        Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new RuntimeException("Auction not found"));

        if(auction.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Auction has ended!");
        }

        if(bidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Bid amount must be greater than zero!");
        }

        Bid bid = new Bid();
        bid.setAmount(bidAmount);
        bid.setTime(LocalDateTime.now());
        bid.setUser(user);
        bid.setAuction(auction);

        bidRepository.save(bid);

        auction.setCurrentPrice(bidAmount);
        auctionRepository.save(auction);

        return bid;
    }


    public List<Bid> findBidsByUser(User user) {
        return this.bidRepository.findByUserId(user.getId());
    }
}
