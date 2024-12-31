package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.model.DTO.UserRegisterDTO;
import com.springproject.auctionplatform.model.entity.User;

public interface UserService {
    User getUserByUsername(String username);
    User getUserById(long id);
    void createUser(UserRegisterDTO registerDTO);
    User updateUser(User user);
    User deleteUser(long id);
}
