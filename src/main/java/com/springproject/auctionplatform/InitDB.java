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

            Auction auction4 = new Auction();
            auction4.setTitle("Vintage 70's Vinyl Record Collection");
            auction4.setStartTime(LocalDateTime.now());
            auction4.setEndTime(LocalDateTime.now().plusDays(7));
            auction4.setDescription("A collection of classic vinyl records from the 70's in excellent condition.");
            auction4.setStatus(AuctionStatus.ONGOING);
            auction4.setCategory(AuctionCategory.COLLECTIBLES);
            auction4.getImageURLs().add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8i4zPog-0j0JR_yZglxPhTPZXxN2iMTQ3Dw&s");
            auction4.setStartingPrice(BigDecimal.valueOf(500L));
            auction4.setCurrentPrice(auction4.getStartingPrice());
            auction4.setSeller(user2);
            auction4.setPromoted(false);
            this.auctionRepository.save(auction4);

            Auction auction5 = new Auction();
            auction5.setTitle("Limited Edition Sneakers");
            auction5.setStartTime(LocalDateTime.now());
            auction5.setEndTime(LocalDateTime.now().plusDays(3));
            auction5.setDescription("A pair of limited edition sneakers, size 10. Never worn.");
            auction5.setStatus(AuctionStatus.ONGOING);
            auction5.setCategory(AuctionCategory.FASHION);
            auction5.getImageURLs().add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8i4zPog-0j0JR_yZglxPhTPZXxN2iMTQ3Dw&s");
            auction5.setStartingPrice(BigDecimal.valueOf(200L));
            auction5.setCurrentPrice(auction5.getStartingPrice());
            auction5.setSeller(user2);
            auction5.setPromoted(false);
            this.auctionRepository.save(auction5);

            Auction auction6 = new Auction();
            auction6.setTitle("Antique Pocket Watch");
            auction6.setStartTime(LocalDateTime.now());
            auction6.setEndTime(LocalDateTime.now().plusDays(10));
            auction6.setDescription("An antique pocket watch from the 19th century. In working condition.");
            auction6.setStatus(AuctionStatus.ONGOING);
            auction6.setCategory(AuctionCategory.COLLECTIBLES);
            auction6.getImageURLs().add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8i4zPog-0j0JR_yZglxPhTPZXxN2iMTQ3Dw&s");
            auction6.setStartingPrice(BigDecimal.valueOf(750L));
            auction6.setCurrentPrice(auction6.getStartingPrice());
            auction6.setSeller(user2);
            auction6.setPromoted(false);
            this.auctionRepository.save(auction6);

            Auction auction7 = new Auction();
            auction7.setTitle("Brand New iPhone 13");
            auction7.setStartTime(LocalDateTime.now());
            auction7.setEndTime(LocalDateTime.now().plusDays(2));
            auction7.setDescription("Brand new, sealed iPhone 13, 128GB. Still in original packaging.");
            auction7.setStatus(AuctionStatus.ONGOING);
            auction7.setCategory(AuctionCategory.ELECTRONICS);
            auction7.getImageURLs().add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8i4zPog-0j0JR_yZglxPhTPZXxN2iMTQ3Dw&s");
            auction7.setStartingPrice(BigDecimal.valueOf(800L));
            auction7.setCurrentPrice(auction7.getStartingPrice());
            auction7.setSeller(user2);
            auction7.setPromoted(false);
            this.auctionRepository.save(auction7);

            Auction auction8 = new Auction();
            auction8.setTitle("Smart Home Security Camera System");
            auction8.setStartTime(LocalDateTime.now());
            auction8.setEndTime(LocalDateTime.now().plusDays(6));
            auction8.setDescription("A set of 4 smart security cameras with mobile app integration.");
            auction8.setStatus(AuctionStatus.ONGOING);
            auction8.setCategory(AuctionCategory.FURNITURE);
            auction8.getImageURLs().add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT8i4zPog-0j0JR_yZglxPhTPZXxN2iMTQ3Dw&s");
            auction8.setStartingPrice(BigDecimal.valueOf(300L));
            auction8.setCurrentPrice(auction8.getStartingPrice());
            auction8.setSeller(user2);
            auction8.setPromoted(false);
            this.auctionRepository.save(auction8);


        }


    }

}
