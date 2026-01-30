package com.sas.demo.domain.product.service.implement;

import com.sas.demo.domain.entity.User;
import com.sas.demo.domain.product.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    @Value ("${jwt.secret:mySecretKey12345678901234567890123456789012}")
    private String secretKey;

    @Value("${jwt.access-token-expiration:3600000}") // 1 gio
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800000}") // 7 ngay
    private long refreshTokenExpiration;

    @Override
    public String generateAccessToken(User user) {
        return generateToken(new HashMap<>(), user, accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(User user) {
        return generateToken(new HashMap<>(), user, refreshTokenExpiration);
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getMail())) && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    @Override
    public long getAccessTokenExpiration() {
        return accessTokenExpiration / 1000; // Return in seconds
    }

    private String generateToken(Map<String, Object> extraClaims, User user, long expiration) {
        extraClaims.put("userId", user.getId().toString());
        extraClaims.put("role", user.getRole().getName());

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getMail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

