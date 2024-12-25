package com.springproject.auctionplatform.repository;

import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(AuctionStatus status);
}
