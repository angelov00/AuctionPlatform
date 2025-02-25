package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.exception.ResourceNotFoundException;
import com.springproject.auctionplatform.model.DTO.*;
import com.springproject.auctionplatform.model.entity.*;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import com.springproject.auctionplatform.model.enums.PaymentMethod;
import com.springproject.auctionplatform.repository.AuctionRepository;
import com.springproject.auctionplatform.repository.BidRepository;
import com.springproject.auctionplatform.repository.PromotionRepository;
import com.springproject.auctionplatform.repository.UserRepository;
import com.springproject.auctionplatform.util.ModelMapper;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

@Transactional
@Service
public class AuctionService {

    private static final BigDecimal PROMOTION_COST_PER_DAY = BigDecimal.TEN;

    private final AuctionRepository auctionRepository;
    private final CloudinaryService cloudinaryService;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Logger logger = LoggerFactory.getLogger(AuctionService.class);
    private final UserRepository userRepository;
    private final BidRepository bidRepository;
    private final PromotionRepository promotionRepository;
    private final ConversationService conversationService;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, CloudinaryService cloudinaryService, UserRepository userRepository, BidRepository bidRepository, PromotionRepository promotionRepository, ConversationService conversationService) {
        this.auctionRepository = auctionRepository;
        this.cloudinaryService = cloudinaryService;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.promotionRepository = promotionRepository;
        this.conversationService = conversationService;
    }

    public Auction createAuction(AuctionAddDTO auctionAddDTO, String username) throws IOException {

        List<String> imageUrls = uploadImages(auctionAddDTO.getImages());

        Auction auction = new Auction();
        auction.setTitle(auctionAddDTO.getTitle());
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(auctionAddDTO.getEndTime());
        auction.setDescription(auctionAddDTO.getDescription());
        auction.setCategory(auctionAddDTO.getCategory());
        auction.setStartingPrice(auctionAddDTO.getStartingPrice());
        auction.setCurrentPrice(auctionAddDTO.getStartingPrice());
        auction.setImageURLs(imageUrls);
        auction.setStatus(AuctionStatus.ONGOING);
        auction.setSeller(userRepository.findByUsername(username).get());

        return auctionRepository.save(auction);
    }

    @Scheduled(fixedRate = 60000) // Изпълнява се всяка минута
    public void updateAuctionStatuses() {
        LocalDateTime now = LocalDateTime.now();

        List<Auction> ongoingAuctions = auctionRepository.findByStatus(AuctionStatus.ONGOING);
        for (Auction auction : ongoingAuctions) {
            if (auction.getEndTime().isBefore(now) || auction.getEndTime().isEqual(now)) {

                auction.setStatus(AuctionStatus.WAITING_FOR_FINALIZATION);
                Optional<User> highestBidder = bidRepository.findByAuctionId(auction.getId()).stream()
                    .max(Comparator.comparing(Bid::getAmount)).map(
                    Bid::getUser);

                if (highestBidder.isPresent()) {
                    Conversation conversation = conversationService
                        .createConversation(auction.getSeller(), highestBidder.get());
                    conversation.setAuction(auction);
                    conversationService.updateConversation(conversation);
                } else {
                    auction.setStatus(AuctionStatus.CANCELLED);
                }

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

    public Auction getAuctionById(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found."));
    }

    public List<BidDetailsDTO> getBidsForAuction(Long auctionId) {
        return bidRepository.findByAuctionId(auctionId).stream().map(ModelMapper::convertBidToBidDetailsDTO).collect(Collectors.toList());
    }


    @Transactional
    public Bid placeBid(Long auctionId, BigDecimal amount, String username) {
        Auction auction = getAuctionById(auctionId);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (Objects.equals(user.getId(), auction.getSeller().getId())) {
            throw new IllegalArgumentException("You cannot place a bid on your own auction!");
        }

        if (auction.getEndTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Auction has ended!");
        }

        if (amount.compareTo(auction.getCurrentPrice()) <= 0) {
            throw new IllegalArgumentException("Bid must be higher than the current price.");
        }

        Bid bid = new Bid();
        bid.setAmount(amount);
        bid.setTime(LocalDateTime.now());
        bid.setUser(user);
        bid.setAuction(auction);
        bidRepository.saveAndFlush(bid);

        auction.setCurrentPrice(amount);
        auctionRepository.saveAndFlush(auction);
        return bid;
    }

    public Optional<Auction> findById(Long auctionId) {
        return this.auctionRepository.findById(auctionId);
    }

    public Page<AuctionPreviewDTO> getPromotedAuctions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("promotedAt").descending());
        Page<Auction> promotedAuctions = auctionRepository.findByIsPromoted(true, pageable);
        return promotedAuctions.map(ModelMapper::convertAuctionToAuctionPreviewDTO);
    }

    public Page<AuctionPreviewDTO> getRegularAuctions(AuctionFilterDTO filter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("endTime").ascending());

        Specification<Auction> specification = createSpecification(filter);
        Page<Auction> auctions = auctionRepository.findAll(specification, pageable);

        return auctions.map(ModelMapper::convertAuctionToAuctionPreviewDTO);
    }

    private Specification<Auction> createSpecification(AuctionFilterDTO filter) {
        return new Specification<Auction>() {
            @Override
            public Predicate toPredicate(Root<Auction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();

                // Филтриране по заглавие
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

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    public Promotion promoteAuction(Long auctionId, PaymentMethod paymentMethod, int promotionDuration) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found."));

        if (auction.getStartTime().plusDays(promotionDuration).isAfter(auction.getEndTime())) {
            throw new IllegalArgumentException("Promotion duration should be less or equal to auction duration.");
        }

        LocalDateTime now = LocalDateTime.now();
        auction.setPromoted(true);
        auction.setPromotedAt(now);
        auction.setPromotionEndTime(now.plusDays(promotionDuration));
        auctionRepository.saveAndFlush(auction);

        Promotion promotion = new Promotion();
        promotion.setPromotionDate(now);
        promotion.setAmount(PROMOTION_COST_PER_DAY.multiply(BigDecimal.valueOf(promotionDuration)));
        promotion.setPaymentMethod(paymentMethod);
        promotion.setAuction(auction);
        promotion.setUser(auction.getSeller());
        promotion.setDuration(promotionDuration);

        promotionRepository.save(promotion);
        return promotion;
    }


    public List<AuctionPreviewDTO> getAuctionsBySellerAndStatus(String username, AuctionStatus status) {
        return auctionRepository.findAllBySellerUsernameAndStatus(username, status).stream().map(ModelMapper::convertAuctionToAuctionPreviewDTO).collect(Collectors.toList());
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

    public long getPromotedAuctionsCount() {
        return this.auctionRepository.countByIsPromoted(true);
    }


    public AuctionDetailsDTO getDetailsById(Long auctionId) {
        Optional<Auction> auction = this.auctionRepository.findById(auctionId);

        if(auction.isPresent()) {
            return ModelMapper.convertAuctionToAuctionDetailsDTO(auction.get());
        } else throw new IllegalArgumentException("Invalid id");
    }

    public long countActiveAuctions() {
        return this.auctionRepository.countAllByStatus(AuctionStatus.ONGOING);
    }

}
