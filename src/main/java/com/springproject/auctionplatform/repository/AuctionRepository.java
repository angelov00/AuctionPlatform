package com.springproject.auctionplatform.repository;

import com.springproject.auctionplatform.model.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
