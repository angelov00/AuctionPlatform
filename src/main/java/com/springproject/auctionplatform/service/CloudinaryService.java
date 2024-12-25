package com.springproject.auctionplatform.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {

    public String uploadImage(MultipartFile file) throws IOException;
}