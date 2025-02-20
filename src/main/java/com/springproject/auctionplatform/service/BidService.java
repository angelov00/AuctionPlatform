package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.model.entity.Bid;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.repository.AuctionRepository;
import com.springproject.auctionplatform.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BidService {

    private final BidRepository bidRepository;

    @Autowired
    public BidService(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public List<Bid> findBidsByUser(User user) {
        return this.bidRepository.findByUserId(user.getId());
    }

    public long getTotalBidsCount() {
        return this.bidRepository.count();
    }
}
