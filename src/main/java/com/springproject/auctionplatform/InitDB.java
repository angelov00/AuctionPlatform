package com.springproject.auctionplatform;

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
    private final BidRepository bidRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public InitDB(UserRepository userRepository, AuctionRepository auctionRepository,
                  PromotionRepository promotionRepository, BidRepository bidRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.promotionRepository = promotionRepository;
        this.bidRepository = bidRepository;
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
            user.getRoles().add(Role.ROLE_ADMIN);
            this.userRepository.save(user);

            User user2 = new User();
            user2.setUsername("jane.smith");
            user2.setPassword(this.passwordEncoder.encode("12345678"));
            user2.setFirstName("Jane");
            user2.setLastName("Smith");
            user2.setEmail("jane.smith@gmail.com");
            user2.setPhone("0777777777");
            this.userRepository.save(user2);

            User user3 = new User();
            user3.setUsername("michael.johnson");
            user3.setPassword(this.passwordEncoder.encode("12345678"));
            user3.setFirstName("Michael");
            user3.setLastName("Johnson");
            user3.setEmail("michael.johnson@gmail.com");
            user3.setPhone("0666666666");
            user3.getRoles().add(Role.ROLE_USER);
            this.userRepository.save(user3);

            User user4 = new User();
            user4.setUsername("emily.davis");
            user4.setPassword(this.passwordEncoder.encode("12345678"));
            user4.setFirstName("Emily");
            user4.setLastName("Davis");
            user4.setEmail("emily.davis@gmail.com");
            user4.setPhone("0555555555");
            user4.getRoles().add(Role.ROLE_USER);
            this.userRepository.save(user4);

            User user5 = new User();
            user5.setUsername("david.wilson");
            user5.setPassword(this.passwordEncoder.encode("12345678"));
            user5.setFirstName("David");
            user5.setLastName("Wilson");
            user5.setEmail("david.wilson@gmail.com");
            user5.setPhone("0444444444");
            user5.getRoles().add(Role.ROLE_USER);
            this.userRepository.save(user5);

            User user6 = new User();
            user6.setUsername("sophia.brown");
            user6.setPassword(this.passwordEncoder.encode("12345678"));
            user6.setFirstName("Sophia");
            user6.setLastName("Brown");
            user6.setEmail("sophia.brown@gmail.com");
            user6.setPhone("0333333333");
            user6.getRoles().add(Role.ROLE_USER);
            this.userRepository.save(user6);

            User user7 = new User();
            user7.setUsername("william.jones");
            user7.setPassword(this.passwordEncoder.encode("12345678"));
            user7.setFirstName("William");
            user7.setLastName("Jones");
            user7.setEmail("william.jones@gmail.com");
            user7.setPhone("0222222222");
            user7.getRoles().add(Role.ROLE_USER);
            this.userRepository.save(user7);

            User user8 = new User();
            user8.setUsername("olivia.miller");
            user8.setPassword(this.passwordEncoder.encode("12345678"));
            user8.setFirstName("Olivia");
            user8.setLastName("Miller");
            user8.setEmail("olivia.miller@gmail.com");
            user8.setPhone("0111111111");
            user8.getRoles().add(Role.ROLE_USER);
            this.userRepository.save(user8);

            User user9 = new User();
            user9.setUsername("james.moore");
            user9.setPassword(this.passwordEncoder.encode("12345678"));
            user9.setFirstName("James");
            user9.setLastName("Moore");
            user9.setEmail("james.moore@gmail.com");
            user9.setPhone("0999999999");
            user9.getRoles().add(Role.ROLE_USER);
            this.userRepository.save(user9);

            User user10 = new User();
            user10.setUsername("charlotte.anderson");
            user10.setPassword(this.passwordEncoder.encode("12345678"));
            user10.setFirstName("Charlotte");
            user10.setLastName("Anderson");
            user10.setEmail("charlotte.anderson@gmail.com");
            user10.setPhone("0888888888");
            user10.getRoles().add(Role.ROLE_USER);
            this.userRepository.save(user10);

            User user11 = new User();
            user11.setUsername("henry.thomas");
            user11.setPassword(this.passwordEncoder.encode("12345678"));
            user11.setFirstName("Henry");
            user11.setLastName("Thomas");
            user11.setEmail("henry.thomas@gmail.com");
            user11.setPhone("0777777777");
            user11.getRoles().add(Role.ROLE_USER);
            this.userRepository.save(user11);

            User user12 = new User();
            user12.setUsername("amelia.white");
            user12.setPassword(this.passwordEncoder.encode("12345678"));
            user12.setFirstName("Amelia");
            user12.setLastName("White");
            user12.setEmail("amelia.white@gmail.com");
            user12.setPhone("0666666666");
            user12.getRoles().add(Role.ROLE_USER);
            this.userRepository.save(user12);


            Auction auction = new Auction();
            auction.setTitle("An old watch");
            auction.setStartTime(LocalDateTime.now());
            auction.setEndTime(LocalDateTime.now().plusMinutes(5));
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

            Auction auction9 = new Auction();
            auction9.setTitle("Vintage Camera");
            auction9.setStartTime(LocalDateTime.now());
            auction9.setEndTime(LocalDateTime.now().plusMinutes(2));
            auction9.setDescription("Classic Polaroid camera, fully functional and in excellent condition.");
            auction9.setStatus(AuctionStatus.ONGOING);
            auction9.setCategory(AuctionCategory.COLLECTIBLES);
            auction9.getImageURLs().add("https://filmcamerastore.co.uk/cdn/shop/files/polaroid-supercolour-600-instant-film-camera-1.jpg?v=1689264754&width=1406");
            auction9.setStartingPrice(BigDecimal.valueOf(100L));
            auction9.setCurrentPrice(auction9.getStartingPrice());
            auction9.setSeller(user2);
            auction9.setPromoted(false);
            this.auctionRepository.save(auction9);

            Bid bid1 = new Bid();
            bid1.setAmount(BigDecimal.valueOf(110L));
            bid1.setUser(user);
            bid1.setAuction(auction9);
            bid1.setTime(LocalDateTime.now());
            this.bidRepository.save(bid1);

            Auction auction10 = new Auction();
            auction10.setTitle("Hand-Knitted Wool Scarf");
            auction10.setStartTime(LocalDateTime.now());
            auction10.setEndTime(LocalDateTime.now().plusMinutes(2));
            auction10.setDescription("Beautifully hand-knitted scarf, made with 100% merino wool.  Perfect for staying warm in style.");
            auction10.setStatus(AuctionStatus.ONGOING);
            auction10.setCategory(AuctionCategory.FASHION);
            auction10.getImageURLs().add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTBA_fpXVa2rQ6t0SlMT5BK8R_Ec2mWbj6eJw&s");
            auction10.setStartingPrice(BigDecimal.valueOf(30L));
            auction10.setCurrentPrice(auction10.getStartingPrice());
            auction10.setSeller(user2);
            auction10.setPromoted(false);
            this.auctionRepository.save(auction10);
        }

    }

}
