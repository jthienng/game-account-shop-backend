package com.sas.demo.domain.product.service.implement;

import com.sas.demo.domain.entity.User;
import com.sas.demo.domain.product.dto.response.UserResponse;
import com.sas.demo.domain.product.service.UserService;
import com.sas.demo.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với id: " + id));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserByMail(String mail) {
        User user = userRepository.findByMail(mail)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + mail));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User chưa đăng nhập");
        }

        String mail = authentication.getName();
        return getUserByMail(mail);
    }

    @Override
    public User findByMail(String mail) {
        return userRepository.findByMail(mail)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + mail));
    }

    @Override
    public boolean existsByMail(String mail) {
        return userRepository.existsByMail(mail);
    }

    @Override
    @Transactional
    public void updateLastLogin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với id: " + userId));
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username ở đây thực chất là email
        return userRepository.findByMail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user với email: " + username));
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setMail(user.getMail());
        response.setRole(user.getRole().getName());
        response.setEnabled(user.getEnabled());
        response.setLastLogin(user.getLastLogin());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
