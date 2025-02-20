package com.springproject.auctionplatform.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class BannedUserFilter extends OncePerRequestFilter {

    public static final String ALLOWED_URL = "/home";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String requestURI = request.getRequestURI();

            boolean isSensitive = !requestURI.contains(ALLOWED_URL);

            if (isSensitive && userDetails.isBanned()) {
                response.sendRedirect(ALLOWED_URL);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}

