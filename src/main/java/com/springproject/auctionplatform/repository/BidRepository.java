package com.springproject.auctionplatform.repository;

import com.springproject.auctionplatform.model.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
}
