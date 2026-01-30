package com.sas.demo.domain.product.service;

import com.sas.demo.domain.entity.RefreshToken;
import com.sas.demo.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user, String token);
    Optional<RefreshToken> findByToken(String token);
    void revokeToken(String token);
    void revokeAllUserTokens(UUID userId);
    boolean isTokenValid(RefreshToken token);
}
