package com.sas.demo.domain.product.service.implement;

import com.sas.demo.domain.entity.User;
import com.sas.demo.domain.product.dto.response.UserResponse;
import com.sas.demo.domain.product.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

public class UserServiceImpl implements UserService {
    @Override
    public UserResponse getUserById(UUID id) {
        return null;
    }

    @Override
    public UserResponse getUserByMail(String mail) {
        return null;
    }

    @Override
    public UserResponse getCurrentUser() {
        return null;
    }

    @Override
    public User findByMail(String mail) {
        return null;
    }

    @Override
    public boolean existsByMail(String mail) {
        return false;
    }

    @Override
    public void updateLastLogin(UUID userId) {

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
