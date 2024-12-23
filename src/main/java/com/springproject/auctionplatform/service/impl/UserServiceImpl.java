package com.springproject.auctionplatform.service.impl;

import com.springproject.auctionplatform.model.DTO.UserLoginDTO;
import com.springproject.auctionplatform.model.DTO.UserRegisterDTO;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.repository.UserRepository;
import com.springproject.auctionplatform.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            .orElseThrow(() -> new EntityNotFoundException(String.format("Username %s not found", username)));
    }

    @Override
    public User getUserById(long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("User with id %s not found", id)));
    }

    @Override
    public User createUser(UserRegisterDTO registerDTO) {
        userRepository.findByUsername(registerDTO.getUsername()).ifPresent(u -> { throw new EntityExistsException(
                String.format("User with username %s already exists", registerDTO.getUsername()));});

        User user = new User(registerDTO.getUsername(), registerDTO.getPassword(), registerDTO.getFirstName(),
            registerDTO.getLastName(), registerDTO.getEmail(), registerDTO.getPhone());

        return userRepository.save(user);
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

    // TODO create AuthService and move this method there?
    @Override
    public User validateLogin(UserLoginDTO loginDTO) {
        System.out.println(loginDTO);
        User existing;
        try {
            existing = getUserByUsername(loginDTO.getUsername());
        } catch (EntityNotFoundException e) {
            return null;
        }
        System.out.println("In service: " + existing);

        return existing.getPassword().equals(loginDTO.getPassword()) ? existing : null;
    }
}
