package com.springproject.auctionplatform.service.impl;

import com.springproject.auctionplatform.model.DTO.AuctionAddDTO;
import com.springproject.auctionplatform.model.DTO.AuctionFilterDTO;
import com.springproject.auctionplatform.model.DTO.AuctionPreviewDTO;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.Bid;
import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import com.springproject.auctionplatform.model.enums.PaymentMethod;
import com.springproject.auctionplatform.repository.AuctionRepository;
import com.springproject.auctionplatform.repository.BidRepository;
import com.springproject.auctionplatform.repository.PromotionRepository;
import com.springproject.auctionplatform.repository.UserRepository;
import com.springproject.auctionplatform.service.CloudinaryService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Service
public class AuctionServiceImpl {

    private final AuctionRepository auctionRepository;
    private final CloudinaryService cloudinaryService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Logger logger = LoggerFactory.getLogger(AuctionServiceImpl.class);
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final PromotionRepository promotionRepository;

    @Autowired
    public AuctionServiceImpl(AuctionRepository auctionRepository, CloudinaryService cloudinaryService, UserRepository userRepository, BidRepository bidRepository, PromotionRepository promotionRepository) {
        this.auctionRepository = auctionRepository;
        this.cloudinaryService = cloudinaryService;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.promotionRepository = promotionRepository;
    }

    public Auction createAuction(AuctionAddDTO auctionAddDTO, String username) throws IOException {

        List<String> imageUrls = uploadImages(auctionAddDTO.getImages());

        Auction auction = new Auction();
        auction.setTitle(auctionAddDTO.getTitle());
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(LocalDateTime.now().plusDays(auctionAddDTO.getDurationDays()));
        auction.setDescription(auctionAddDTO.getDescription());
        auction.setCategory(auctionAddDTO.getCategory());
        auction.setStartingPrice(auctionAddDTO.getStartingPrice());
        auction.setCurrentPrice(auctionAddDTO.getStartingPrice());
        auction.setImageURLs(imageUrls);
        auction.setStatus(AuctionStatus.UPCOMING);
        auction.setSeller(userRepository.findByUsername(username).get());

        auction.setStatus(AuctionStatus.ONGOING);

        return auctionRepository.save(auction);
    }

    private void activateAuction(Auction auction) {
        auction.setStatus(AuctionStatus.ONGOING);  // Променяме статуса на активен
        auctionRepository.save(auction);  // Записваме аукциона в базата
        logger.info("Auction '{}' has been activated.", auction.getTitle());
    }


    @Scheduled(fixedRate = 60000) // Изпълнява се всяка минута
    public void updateAuctionStatuses() {
        LocalDateTime now = LocalDateTime.now();

        List<Auction> upcomingAuctions = auctionRepository.findByStatus(AuctionStatus.UPCOMING);
        for (Auction auction : upcomingAuctions) {
            if (auction.getStartTime().isBefore(now) || auction.getStartTime().isEqual(now)) {
                auction.setStatus(AuctionStatus.ONGOING);
                auctionRepository.save(auction);
            }
        }

        List<Auction> ongoingAuctions = auctionRepository.findByStatus(AuctionStatus.ONGOING);
        for (Auction auction : ongoingAuctions) {
            if (auction.getEndTime().isBefore(now) || auction.getEndTime().isEqual(now)) {
                auction.setStatus(AuctionStatus.WAITING_FOR_FINALIZATION);
                auctionRepository.save(auction);
            }
        }

        List<Auction> promotedAuctions = auctionRepository.findByIsPromoted(true);
        for(Auction auction : promotedAuctions) {
            if(auction.getPromotionEndTime().isBefore(now) || auction.getPromotionEndTime().isEqual(now)) {
                auction.setPromoted(false);
                auction.setPromotedAt(null);
                auction.setPromotionEndTime(null);
                auctionRepository.save(auction);
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

        if (auction.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Auction has ended!");
        }

        if (amount.compareTo(auction.getCurrentPrice()) <= 0) {
            throw new IllegalArgumentException("Bid must be higher than the current price.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        System.out.println("COMPARING: " + user.getId() + " AND " + auction.getSeller().getId());
        if (Objects.equals(user.getId(), auction.getSeller().getId())) {
           throw new IllegalArgumentException("You cannot place a bid on your own auction!");
        }

        Bid bid = new Bid();
        bid.setAmount(amount);
        bid.setTime(LocalDateTime.now());
        bid.setUser(user);
        bid.setAuction(auction);
        bidRepository.saveAndFlush(bid);
        auction.setCurrentPrice(amount);
        auctionRepository.saveAndFlush(auction);
    }

    public List<Auction> getAuctionsBySeller(String username) {
        return auctionRepository.findBySeller_Username(username);
    }


    public Optional<Auction> findById(Long auctionId) {
        return this.auctionRepository.findById(auctionId);
    }

    // Метод за получаване на промотирани търгове
    public Page<AuctionPreviewDTO> getPromotedAuctions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("promotedAt").descending());
        Page<Auction> promotedAuctions = auctionRepository.findByIsPromoted(true, pageable);
        return promotedAuctions.map(this::convertToDTO);
    }

    // Метод за получаване на редовни търгове с филтри
    public Page<AuctionPreviewDTO> getRegularAuctions(AuctionFilterDTO filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endTime").ascending());

        Specification<Auction> specification = createSpecification(filter);
        Page<Auction> auctions = auctionRepository.findAll(specification, pageable);
        return auctions.map(this::convertToDTO);
    }

    // Метод за създаване на Specification за филтри
    private Specification<Auction> createSpecification(AuctionFilterDTO filter) {
        return new Specification<Auction>() {
            @Override
            public Predicate toPredicate(Root<Auction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                // Филтриране по заглавие (ако има подаден текст за търсене)
                if (filter.getTitleSearch() != null && !filter.getTitleSearch().isEmpty()) {
                    Predicate titlePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + filter.getTitleSearch().toLowerCase() + "%");
                    predicates.add(titlePredicate);
                }

                // Филтриране по категория
                if (filter.getAuctionCategory() != null) {
                    Predicate categoryPredicate = criteriaBuilder.equal(root.get("category"), filter.getAuctionCategory());
                    predicates.add(categoryPredicate);
                }

                // Филтриране по минимална цена
                if (filter.getCurrentPriceGreaterThan() != null) {
                    Predicate minPricePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("currentPrice"), filter.getCurrentPriceGreaterThan());
                    predicates.add(minPricePredicate);
                }

                // Филтриране по максимална цена
                if (filter.getCurrentPriceLessThan() != null) {
                    Predicate maxPricePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("currentPrice"), filter.getCurrentPriceLessThan());
                    predicates.add(maxPricePredicate);
                }

                // Връщане на финалното предикат (условие) за запитването
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    // Метод за конвертиране на Auction в AuctionPreviewDTO
    private AuctionPreviewDTO convertToDTO(Auction auction) {
        return new AuctionPreviewDTO(
                auction.getId(),
                auction.getTitle(),
                auction.getEndTime(),
                auction.getDescription(),
                auction.getCategory(),
                auction.getImageURLs().getFirst(),
                auction.getCurrentPrice()
        );
    }

    public Promotion promoteAuction(Long auctionId, PaymentMethod paymentMethod, int promotionDuration) {

        final int costPerDay = 10;

        Optional<Auction> auction = this.auctionRepository.findById(auctionId);
        Promotion promotion = new Promotion();

        if(auction.isPresent()) {
            auction.get().setPromoted(true);
            auction.get().setPromotedAt(LocalDateTime.now());
            auction.get().setPromotionEndTime(LocalDateTime.now().plusDays(promotionDuration));
            auctionRepository.saveAndFlush(auction.get());

            promotion.setPromotionDate(LocalDateTime.now());
            promotion.setAmount(BigDecimal.valueOf((long) promotionDuration * costPerDay));
            promotion.setPaymentMethod(paymentMethod);
            promotion.setAuction(auction.get());
            promotion.setUser(auction.get().getSeller());
            promotion.setDuration(promotionDuration);
            this.promotionRepository.save(promotion);

        } else {
            throw new IllegalArgumentException("Auction not found!");
        }

        return promotion;
    }

    public List<Auction> getAuctionsBySellerAndStatus(String username, AuctionStatus status) {
        return auctionRepository.findAllBySellerUsernameAndStatus(username, status);
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
