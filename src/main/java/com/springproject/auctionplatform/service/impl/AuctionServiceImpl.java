package com.springproject.auctionplatform.service.impl;

import com.springproject.auctionplatform.model.DTO.AuctionAddDTO;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.Bid;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import com.springproject.auctionplatform.repository.AuctionRepository;
import com.springproject.auctionplatform.repository.BidRepository;
import com.springproject.auctionplatform.repository.UserRepository;
import com.springproject.auctionplatform.service.CloudinaryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AuctionServiceImpl {

    private final AuctionRepository auctionRepository;
    private final CloudinaryService cloudinaryService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Logger logger = LoggerFactory.getLogger(AuctionServiceImpl.class);
    private final UserRepository userRepository;
    private final BidRepository bidRepository;

    @Autowired
    public AuctionServiceImpl(AuctionRepository auctionRepository, CloudinaryService cloudinaryService, UserRepository userRepository, BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.cloudinaryService = cloudinaryService;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
    }

    public Auction createAuction(AuctionAddDTO auctionAddDTO, String username) throws IOException {

        List<String> imageUrls = uploadImages(auctionAddDTO.getImages());

        Auction auction = new Auction();
        auction.setTitle(auctionAddDTO.getTitle());
        auction.setStartTime(auctionAddDTO.getStartTime());
        auction.setEndTime(auctionAddDTO.getEndTime());
        auction.setDescription(auctionAddDTO.getDescription());
        auction.setCategory(auctionAddDTO.getCategory());
        auction.setStartingPrice(auctionAddDTO.getStartingPrice());
        auction.setReservePrice(auctionAddDTO.getReservePrice());
        auction.setCurrentPrice(auctionAddDTO.getStartingPrice());
        auction.setImageURLs(imageUrls);
        auction.setStatus(AuctionStatus.UPCOMING);
        auction.setSeller(userRepository.findByUsername(username).get());

        if (auction.getStartTime().isAfter(LocalDateTime.now())) {
            auction.setStatus(AuctionStatus.UPCOMING);
        } else {
            auction.setStatus(AuctionStatus.ONGOING);
        }

        return auctionRepository.save(auction);
    }


    private void scheduleAuctionStart(Auction auction) {
        LocalDateTime startTime = auction.getStartTime();
        LocalDateTime now = LocalDateTime.now();

        if (startTime.isAfter(now)) {
            long delay = Duration.between(now, startTime).toMillis();

            scheduler.schedule(() -> {
                activateAuction(auction);
            }, delay, TimeUnit.MILLISECONDS);
        } else {
            activateAuction(auction);
        }
    }

    private void activateAuction(Auction auction) {
        auction.setStatus(AuctionStatus.ONGOING);  // Променяме статуса на активен
        auctionRepository.save(auction);  // Записваме аукциона в базата
        logger.info("Auction '{}' has been activated.", auction.getTitle());
    }


    private List<String> uploadImages(List<MultipartFile> images) throws IOException {
        return images.stream()
                .map(image -> {
                    try {
                        return cloudinaryService.uploadImage(image);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to upload image", e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 60000) // Изпълнява се всяка минута
    public void updateAuctionStatuses() {
        LocalDateTime now = LocalDateTime.now();

        List<Auction> upcomingAuctions = auctionRepository.findByStatus(AuctionStatus.UPCOMING);
        for (Auction auction : upcomingAuctions) {
            if (auction.getStartTime().isBefore(now) || auction.getStartTime().isEqual(now)) {
                auction.setStatus(AuctionStatus.ONGOING);
                auctionRepository.save(auction);
                System.out.println("Auction with id " + auction.getId() + " is now active.");
            }
        }

        List<Auction> ongoingAuctions = auctionRepository.findByStatus(AuctionStatus.ONGOING);
        for (Auction auction : ongoingAuctions) {
            if (auction.getEndTime().isBefore(now) || auction.getEndTime().isEqual(now)) {
                auction.setStatus(AuctionStatus.COMPLETED);
                auctionRepository.save(auction);
                System.out.println("Auction with id " + auction.getId() + " has ended.");
            }
        }
    }

    public List<Auction> findAuctions(String category, BigDecimal minPrice, BigDecimal maxPrice) {
        return auctionRepository.findAll().stream()
                .filter(auction -> category == null || auction.getCategory().name().equalsIgnoreCase(category))
                .filter(auction -> minPrice == null || auction.getCurrentPrice().compareTo(minPrice) >= 0)
                .filter(auction -> maxPrice == null || auction.getCurrentPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());
    }

    public Auction getAuctionById(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found."));
    }

    public List<Bid> getBidsForAuction(Long auctionId) {
        return bidRepository.findByAuctionId(auctionId);
    }

    @Transactional
    public void placeBid(Long auctionId, BigDecimal amount, String username) {
        Auction auction = getAuctionById(auctionId);

//        if (auction.getStatus() != AuctionStatus.ONGOING) {
//            throw new IllegalArgumentException("Auction is not active for bidding.");
//        }

        if (amount.compareTo(auction.getCurrentPrice()) <= 0) {
            throw new IllegalArgumentException("Bid must be higher than the current price.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Bid bid = new Bid();
        bid.setAmount(amount);
        bid.setTime(LocalDateTime.now());
        bid.setUser(user);
        bid.setAuction(auction);

        bidRepository.saveAndFlush(bid);

        auction.setCurrentPrice(amount);
        auctionRepository.saveAndFlush(auction);
    }





}
