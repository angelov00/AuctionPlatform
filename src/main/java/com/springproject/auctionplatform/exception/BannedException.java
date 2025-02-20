package com.springproject.auctionplatform.exception;

import org.springframework.security.core.AuthenticationException;

public class BannedException extends AuthenticationException {
    public BannedException(String message) {
        super(message);
    }
}
