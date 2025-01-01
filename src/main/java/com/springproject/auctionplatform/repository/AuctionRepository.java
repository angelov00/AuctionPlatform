package com.springproject.auctionplatform.repository;

import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(AuctionStatus status);
    List<Auction> findBySeller_Username(String username);
    Page<Auction> findAll(Specification<Auction> specification, Pageable pageable);

    Page<Auction> findByIsPromoted(boolean b, Pageable pageable);
    List<Auction> findByIsPromoted(boolean b);
}
