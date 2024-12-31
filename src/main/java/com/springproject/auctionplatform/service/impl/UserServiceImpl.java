package com.springproject.auctionplatform.service.impl;

import com.springproject.auctionplatform.model.DTO.UserRegisterDTO;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.Role;
import com.springproject.auctionplatform.repository.UserRepository;
import com.springproject.auctionplatform.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Username %s not found", username)));
    }

    @Transactional(readOnly = true)
    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s not found", id)));
    }

    @Override
    public void createUser(UserRegisterDTO registerDTO) {
        userRepository.findByUsername(registerDTO.getUsername()).ifPresent(u -> { throw new EntityExistsException(
                String.format("User with username %s already exists", registerDTO.getUsername()));});

        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        User user = new User(registerDTO.getUsername(), encodedPassword, registerDTO.getFirstName(),
            registerDTO.getLastName(), registerDTO.getEmail(), registerDTO.getPhone(), Set.of(Role.ROLE_USER));

        userRepository.save(user);
    }

    // TODO create UserUpdateDTO ?
    @Override
    public User updateUser(User user) {
        User existing = getUserById(user.getId());

        if (!user.getId().equals(existing.getId())) {
            // TODO create custom exception
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
