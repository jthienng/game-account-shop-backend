package com.sas.demo.domain.product.service.implement;

import com.sas.demo.domain.entity.RefreshToken;
import com.sas.demo.domain.entity.User;
import com.sas.demo.domain.product.service.RefreshTokenService;
import com.sas.demo.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration:604800000}") // 7 ngay
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(UUID.randomUUID());
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiration));
        refreshToken.setRevoked(false);
        refreshToken.setCreateAt(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findAll().stream()
                .filter(rt -> rt.getToken().equals(token))
                .findFirst();
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        findByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshToken.setUpdateAt(Instant.now());
            refreshTokenRepository.save(refreshToken);
        });
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(UUID userId) {
        refreshTokenRepository.findByUserId(userId).forEach(token -> {
            token.setRevoked(true);
            token.setUpdateAt(Instant.now());
            refreshTokenRepository.save(token);
        });
    }

    @Override
    public boolean isTokenValid(RefreshToken token) {
        return token != null
                && !token.getRevoked()
                && token.getExpiresAt().isAfter(Instant.now());
    }
}
