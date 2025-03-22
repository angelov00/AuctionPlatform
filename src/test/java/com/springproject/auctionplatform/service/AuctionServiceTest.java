package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.Bid;
import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import com.springproject.auctionplatform.model.enums.PaymentMethod;
import com.springproject.auctionplatform.model.enums.Role;
import com.springproject.auctionplatform.repository.AuctionRepository;
import com.springproject.auctionplatform.repository.BidRepository;
import com.springproject.auctionplatform.repository.PromotionRepository;
import com.springproject.auctionplatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuctionServiceTest {

    private static final Long AUCTION_ID = 1L;
    private static final Long SELLER_ID = 1L;
    private static final Long BIDDER_ID = 2L;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BidRepository bidRepository;

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private AuctionService auctionService;

    private Auction testAuction;
    private Bid testBid;
    private User seller;
    private User bidder;

    @BeforeEach
    void setUp() {
        seller = createUser("testUser", SELLER_ID, "test@example.com");
        bidder = createUser("testUser2", BIDDER_ID, "test2@example.com");

        testAuction = createAuction(AUCTION_ID, seller);
        testBid = createBid(1L, testAuction, bidder, BigDecimal.valueOf(200L));
    }

    @Test
    void placeBid_ValidBid_ShouldReturnBid() {
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuction));
        when(userRepository.findByUsername(bidder.getUsername())).thenReturn(Optional.of(bidder));
        when(bidRepository.saveAndFlush(any(Bid.class))).thenReturn(testBid);

        Bid actualBid = auctionService.placeBid(AUCTION_ID, BigDecimal.valueOf(200L), bidder.getUsername());

        assertNotNull(actualBid);
        assertEquals(BigDecimal.valueOf(200L), actualBid.getAmount());
        assertEquals(bidder.getUsername(), actualBid.getUser().getUsername());

        verify(bidRepository, times(1)).saveAndFlush(any(Bid.class));
    }

    @Test
    void placeBid_BidOnOwnAuction_ShouldThrowException() {
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuction));
        when(userRepository.findByUsername(seller.getUsername())).thenReturn(Optional.of(seller));

        assertThrows(IllegalArgumentException.class, () ->
                auctionService.placeBid(AUCTION_ID, BigDecimal.valueOf(200L), seller.getUsername())
        );

        verify(bidRepository, never()).saveAndFlush(any(Bid.class));
        verify(auctionRepository, never()).saveAndFlush(any(Auction.class));
    }

    @Test
    void placeBid_AuctionHasEnded_ShouldThrowException() {
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuction));
        when(userRepository.findByUsername(bidder.getUsername())).thenReturn(Optional.of(bidder));

        testAuction.setEndTime(LocalDateTime.now().minusMinutes(1));

        assertThrows(IllegalArgumentException.class, () ->
                auctionService.placeBid(AUCTION_ID, BigDecimal.valueOf(2000L), bidder.getUsername())
        );

        verify(bidRepository, never()).saveAndFlush(any(Bid.class));
        verify(auctionRepository, never()).saveAndFlush(any(Auction.class));
    }

    @Test
    void placeBid_LowerThanCurrentPrice_ShouldThrowException() {
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.of(testAuction));
        when(userRepository.findByUsername(bidder.getUsername())).thenReturn(Optional.of(bidder));

        assertThrows(IllegalArgumentException.class, () ->
                auctionService.placeBid(AUCTION_ID, BigDecimal.valueOf(2L), bidder.getUsername())
        );

        verify(bidRepository, never()).saveAndFlush(any(Bid.class));
        verify(auctionRepository, never()).saveAndFlush(any(Auction.class));
    }

    @Test
    void promoteAuction_InvalidDuration_ShouldThrowException() {
        when(auctionRepository.findById(testAuction.getId())).thenReturn(Optional.of(testAuction));

        assertThrows(IllegalArgumentException.class, () ->
                auctionService.promoteAuction(testAuction.getId(), PaymentMethod.PAYPAL, 40)
        );

        verify(promotionRepository, never()).save(any(Promotion.class));
        verify(auctionRepository, never()).saveAndFlush(any(Auction.class));
    }

    @Test
    void promoteAuction_InvalidAuction_ShouldThrowException() {
        when(auctionRepository.findById(AUCTION_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                auctionService.promoteAuction(AUCTION_ID, PaymentMethod.BANK_TRANSFER, 20)
        );

        verify(promotionRepository, never()).save(any(Promotion.class));
        verify(auctionRepository, never()).saveAndFlush(any(Auction.class));
    }

    // Помощни методи за създаване на обекти

    private User createUser(String username, Long id, String email) {
        User user = new User(username, "encodedPassword", "Test", "User", email, "123456789", Set.of(Role.ROLE_USER));
        user.setId(id);
        return user;
    }

    private Auction createAuction(Long id, User seller) {
        Auction auction = new Auction();
        auction.setId(id);
        auction.setTitle("Test Auction");
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(LocalDateTime.now().plusDays(7));
        auction.setDescription("This is a test auction.");
        auction.setStatus(AuctionStatus.ONGOING);
        auction.setCategory(AuctionCategory.ELECTRONICS);
        auction.setStartingPrice(new BigDecimal("100.00"));
        auction.setCurrentPrice(new BigDecimal("150.00"));
        auction.setSeller(seller);
        auction.setImageURLs(List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg"));
        return auction;
    }

    private Bid createBid(Long id, Auction auction, User bidder, BigDecimal amount) {
        Bid bid = new Bid();
        bid.setId(id);
        bid.setAuction(auction);
        bid.setUser(bidder);
        bid.setAmount(amount);
        return bid;
    }
}
