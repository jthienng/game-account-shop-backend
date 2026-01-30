package com.sas.demo.domain.product.service;

import com.sas.demo.domain.entity.User;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String extractUsername(String token);
    boolean isTokenValid(String token, User user);
    boolean isTokenExpired(String token);
    long getAccessTokenExpiration();
}
