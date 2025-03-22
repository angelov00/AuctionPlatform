package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.exception.ResourceNotFoundException;
import com.springproject.auctionplatform.model.DTO.AuctionPreviewDTO;
import com.springproject.auctionplatform.model.DTO.UserRegisterDTO;
import com.springproject.auctionplatform.model.DTO.UserUpdateDTO;
import com.springproject.auctionplatform.model.entity.Auction;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.Role;
import com.springproject.auctionplatform.repository.AuctionRepository;
import com.springproject.auctionplatform.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional
@Service
public class UserService{

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, AuctionRepository auctionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Username %s not found", username)));
    }

    @Transactional(readOnly = true)
    public User getUserById(long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id %s not found", id)));
    }

    public void createUser(UserRegisterDTO registerDTO) {
        userRepository.findByUsername(registerDTO.getUsername()).ifPresent(u -> { throw new EntityExistsException(
                String.format("User with username %s already exists", registerDTO.getUsername()));});

        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        User user = new User(registerDTO.getUsername(), encodedPassword, registerDTO.getFirstName(),
            registerDTO.getLastName(), registerDTO.getEmail(), registerDTO.getPhone(), Set.of(Role.ROLE_USER));

        userRepository.save(user);
    }

    public void addToWatchlist(User user, Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));

        user.getWatchlist().add(auction);
        userRepository.save(user);
    }

    public Auction removeFromWatchlist(User user, Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));

        user.getWatchlist().remove(auction);
        userRepository.save(user);
        return auction;
    }

    public List<AuctionPreviewDTO> getWatchlist(User user) {
        return user.getWatchlist().stream().map(auction -> new AuctionPreviewDTO(
                auction.getId(),
                auction.getTitle(),
                auction.getEndTime(),
                auction.getCompletionTime(),
                auction.getDescription(),
                auction.getCategory(),
                auction.getImageURLs().getFirst(),
                auction.getCurrentPrice(),
                auction.getSeller().getId(),
                auction.getBuyer() == null ? -1 : auction.getBuyer().getId(),
                auction.getStartingPrice())).toList();
    }

    public User updateUserProfile(User currentUser, UserUpdateDTO updatedUser) {
        currentUser.setFirstName(updatedUser.getFirstName());
        currentUser.setLastName(updatedUser.getLastName());
        currentUser.setEmail(updatedUser.getEmail());
        currentUser.setPhone(updatedUser.getPhone());
        //currentUser.setAvatarURL(updatedUser.getAvatarURL());

        return userRepository.save(currentUser); // Записваме промените в базата данни
    }

    public void promoteToAdmin(Long userId) {
        User user = getUserById(userId);
        user.getRoles().add(Role.ROLE_ADMIN);
        this.userRepository.save(user);
    }

    public void banUser(Long userId) {
        User user = getUserById(userId);
        user.setBanned(true);
        this.userRepository.save(user);
    }

    public void unbanUser(Long userId) {
        User user = getUserById(userId);
        user.setBanned(false);
        this.userRepository.save(user);
    }

    public long countUsers() {
        return this.userRepository.count();
    }

    public long countBannedUser() {
        return this.userRepository.countBannedUsers();
    }

    public List<User> findAllList() {
        return this.userRepository.findAll();
    }
}
