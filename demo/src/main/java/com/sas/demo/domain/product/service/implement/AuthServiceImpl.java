package com.sas.demo.domain.product.service.implement;

import com.sas.demo.domain.product.dto.request.LoginRequest;
import com.sas.demo.domain.product.dto.request.RegisterRequest;
import com.sas.demo.domain.product.dto.response.AuthResponse;
import com.sas.demo.domain.product.service.AuthService;


public class AuthServiceImpl implements AuthService {
    @Override
    public AuthResponse register(RegisterRequest request) {
        return null;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        return null;
    }

    @Override
    public void logout(String refreshToken) {

    }

    @Override
    public void logoutAll(String userId) {

    }
}