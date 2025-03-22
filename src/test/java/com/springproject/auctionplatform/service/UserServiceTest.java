package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.exception.ResourceNotFoundException;
import com.springproject.auctionplatform.model.DTO.UserRegisterDTO;
import com.springproject.auctionplatform.model.entity.User;
import com.springproject.auctionplatform.model.enums.Role;
import com.springproject.auctionplatform.repository.AuctionRepository;
import com.springproject.auctionplatform.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "encodedPassword", "Test", "User", "test@example.com", "123456789", Set.of(Role.ROLE_USER));
        testUser.setId(1L);
    }

    @Test
    void getUserById_UserExists_ShouldReturnUser() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        User user = userService.getUserById(testUser.getId());

        assertEquals(testUser, user);
    }

    @Test
    void getUserById_UserDoesNotExist_ShouldThrow() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(testUser.getId()));
    }


    @Test
    void getUserByUsername_UserExists_ShouldReturnUser() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        User user = userService.getUserByUsername("testUser");

        assertNotNull(user);
        assertEquals(testUser.getUsername(), user.getUsername());
    }

    @Test
    void getByUsername_UserDoesNotExist_ShouldThrowsException() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername("testUser"));
    }

    @Test
    void createUser_UserDoesNotExist_ShouldSaveUser() {
        UserRegisterDTO dto = new UserRegisterDTO("newUser", "password", "First", "Last", "new@example.com", "987654321");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded_password");

        userService.createUser(dto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_UserExists_ShouldThrowException() {
        UserRegisterDTO dto = new UserRegisterDTO("testUser", "password", "First", "Last", "new@example.com", "987654321");

        when(userRepository.findByUsername(dto.getUsername())).thenReturn(Optional.of(testUser));

        assertThrows(EntityExistsException.class, () -> userService.createUser(dto));
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(dto.getPassword());
    }
}
