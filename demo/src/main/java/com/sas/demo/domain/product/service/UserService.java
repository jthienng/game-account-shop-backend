package com.sas.demo.domain.product.service;

import com.sas.demo.domain.entity.User;
import com.sas.demo.domain.product.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.UUID;

public interface UserService extends UserDetailsService {
    UserResponse getUserById(UUID id);

    UserResponse getUserByMail(String mail);

    UserResponse getCurrentUser();

    User findByMail(String mail);

    boolean existsByMail(String mail);

    void updateLastLogin(UUID userId);
}