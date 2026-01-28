package com.sas.demo.domain.product.service;

import com.sas.demo.domain.product.dto.request.LoginRequest;
import com.sas.demo.domain.product.dto.request.RegisterRequest;
import com.sas.demo.domain.product.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(String refreshToken);

    void logout(String refreshToken);

    void logoutAll(String userId);
}
