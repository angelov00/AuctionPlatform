package com.springproject.auctionplatform.model.entity;

import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name="auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionCategory category;

    @ElementCollection
    private List<String> imageURLs;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal startingPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal currentPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal reservePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="seller_id", nullable = false)
    private User seller;
}