package com.sas.demo.domain.repo;

import com.sas.demo.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, UUID> {
    List<RefreshToken> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
