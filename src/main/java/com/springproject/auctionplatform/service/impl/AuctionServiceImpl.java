package com.springproject.auctionplatform.service.impl;

import com.springproject.auctionplatform.model.DTO.AuctionAddDTO;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import com.springproject.auctionplatform.repository.AuctionRepository;
import com.springproject.auctionplatform.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    @Autowired
    public AuctionServiceImpl(AuctionRepository auctionRepository, CloudinaryService cloudinaryService) {
        this.auctionRepository = auctionRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public Auction createAuction(AuctionAddDTO auctionAddDTO, User user) throws IOException {

        List<String> imageUrls = uploadImages(auctionAddDTO.getImages());

        // Създаване на нов аукцион
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
        auction.setSeller(user);

        if (auction.getStartTime().isAfter(LocalDateTime.now())) {
            auction.setStatus(AuctionStatus.UPCOMING);
        } else {
            auction.setStatus(AuctionStatus.ONGOING);
        }

        return auctionRepository.save(auction);
    }


    // Метод за планиране на стартиране на аукцион
    private void scheduleAuctionStart(Auction auction) {
        LocalDateTime startTime = auction.getStartTime();
        LocalDateTime now = LocalDateTime.now();

        // Ако startTime е в бъдеще
        if (startTime.isAfter(now)) {
            long delay = Duration.between(now, startTime).toMillis(); // Изчисляваме колко милисекунди остават

            // Планираме задачата да се изпълни в точния момент
            scheduler.schedule(() -> {
                activateAuction(auction);
            }, delay, TimeUnit.MILLISECONDS);
        } else {
            // Ако startTime вече е настъпил, активираме аукциона веднага
            activateAuction(auction);
        }
    }

    // Активиране на аукциона, когато настъпи startTime
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


}
