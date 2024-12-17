package com.springproject.auctionplatform.service.impl;

import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.repository.UserRepository;
import com.springproject.auctionplatform.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s not found", id)));
    }

    @Override
    public User createUser(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresent(u -> { throw new EntityExistsException(
                String.format("User with username %s already exists", user.getUsername()));});

        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        User existing = getUserById(user.getId());

        if (!user.getId().equals(existing.getId())) {
            throw new RuntimeException(String.format("User with username %s could not be updated", user.getUsername()));
        }

        return userRepository.save(user);
    }

    @Override
    public User deleteUser(long id) {
        User existing = getUserById(id);

        userRepository.deleteById(id);

        return existing;
    }
}
