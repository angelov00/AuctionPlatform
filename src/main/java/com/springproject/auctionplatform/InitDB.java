package com.springproject.auctionplatform;

import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.Promotion;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.AuctionCategory;
import com.springproject.auctionplatform.model.enums.AuctionStatus;
import com.springproject.auctionplatform.model.enums.PaymentMethod;
import com.springproject.auctionplatform.repository.AuctionRepository;
import com.springproject.auctionplatform.repository.PromotionRepository;
import com.springproject.auctionplatform.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class InitDB {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final PromotionRepository promotionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InitDB(UserRepository userRepository, AuctionRepository auctionRepository, PromotionRepository promotionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.promotionRepository = promotionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {

        if(this.userRepository.count() == 0) {
            User user = new User();
            user.setUsername("john.smith");
            user.setPassword(this.passwordEncoder.encode("12345678"));
            user.setFirstName("John");
            user.setLastName("Smith");
            user.setEmail("john.smith@gmail.com");
            user.setPhone("0888888888");
            user.getPreferredCategories().add(AuctionCategory.ART);
            this.userRepository.save(user);

            User user2 = new User();
            user2.setUsername("jane.smith");
            user2.setPassword(this.passwordEncoder.encode("12345678"));
            user2.setFirstName("Jane");
            user2.setLastName("Smith");
            user2.setEmail("jane.smith@gmail.com");
            user2.setPhone("0777777777");
            user2.getPreferredCategories().add(AuctionCategory.JEWELRY);
            this.userRepository.save(user2);

            Auction auction = new Auction();
            auction.setTitle("An old watch");
            auction.setStartTime(LocalDateTime.now());
            auction.setEndTime(LocalDateTime.now().plusDays(15));
            auction.setDescription("An old watch in very good condition. Mechanical");;
            auction.setStatus(AuctionStatus.ONGOING);
            auction.setCategory(AuctionCategory.JEWELRY);
            auction.getImageURLs().add("https://willem.com/blog/2020-08-23_cleaning-a-vintage-watch/images/Cleaning-a-vintage-watch-2x.jpg");
            auction.setStartingPrice(BigDecimal.valueOf(100L));
            auction.setCurrentPrice(auction.getStartingPrice());
            auction.setSeller(user);
            auction.setPromoted(false);
            this.auctionRepository.save(auction);


            Auction auction2 = new Auction();
            auction2.setTitle("Retro gaming console");
            auction2.setStartTime(LocalDateTime.now());
            auction2.setEndTime(LocalDateTime.now().plusDays(5));
            auction2.setDescription("Atari 2600. In very good condition");
            auction2.setStatus(AuctionStatus.ONGOING);
            auction2.setCategory(AuctionCategory.ELECTRONICS);
            auction2.getImageURLs().add("https://nicole.express/assets/img/2600-tons/atari.JPEG");
            auction2.setStartingPrice(BigDecimal.valueOf(250L));
            auction2.setCurrentPrice(auction2.getStartingPrice());
            auction2.setSeller(user2);
            auction2.setPromoted(false);
            this.auctionRepository.save(auction2);

            Auction auction3 = new Auction();
            auction3.setTitle("Skateboard");
            auction3.setStartTime(LocalDateTime.now());
            auction3.setEndTime(LocalDateTime.now().plusDays(30));
            auction3.setDescription("Skateboard with new wheels and autograph from James Gosling");
            auction3.setStatus(AuctionStatus.ONGOING);
            auction3.setCategory(AuctionCategory.SPORTS);
            auction3.getImageURLs().add("https://rukminim2.flixcart.com/image/850/1000/xif0q/skateboard/p/m/2/skateboard-for-boys-8-year-age-24-funforce-6-original-imagz3wkhzcwfjc9.jpeg?q=90&crop=false");
            auction3.setStartingPrice(BigDecimal.valueOf(45L));
            auction3.setCurrentPrice(auction3.getStartingPrice());
            auction3.setSeller(user2);
            auction3.setPromoted(true);
            auction3.setPromotedAt(LocalDateTime.now());
            auction3.setPromotionEndTime(LocalDateTime.now().plusDays(30));
            this.auctionRepository.save(auction3);

            Promotion promotion = new Promotion();
            promotion.setUser(user2);
            promotion.setAuction(auction3);
            promotion.setPaymentMethod(PaymentMethod.PAYPAL);
            promotion.setPromotionDate(LocalDateTime.now());
            promotion.setDuration(7);
            long price = promotion.getDuration() * 10L;
            promotion.setAmount(BigDecimal.valueOf(price));
            this.promotionRepository.save(promotion);

        }


    }

}
