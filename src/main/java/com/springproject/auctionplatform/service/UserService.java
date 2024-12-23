package com.springproject.auctionplatform.service;

import com.springproject.auctionplatform.model.DTO.UserLoginDTO;
import com.springproject.auctionplatform.model.DTO.UserRegisterDTO;
import com.springproject.auctionplatform.model.entity.User;

public interface UserService {
    User getUserByUsername(String username);
    User getUserById(long id);
    User createUser(UserRegisterDTO registerDTO);
    User updateUser(User user);
    User deleteUser(long id);
    User validateLogin(UserLoginDTO loginDTO);
}
