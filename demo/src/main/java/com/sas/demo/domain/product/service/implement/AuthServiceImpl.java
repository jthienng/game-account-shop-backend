package com.sas.demo.domain.product.service.implement;

import com.sas.demo.domain.entity.RefreshToken;
import com.sas.demo.domain.entity.Role;
import com.sas.demo.domain.entity.User;
import com.sas.demo.domain.product.dto.request.LoginRequest;
import com.sas.demo.domain.product.dto.request.RegisterRequest;
import com.sas.demo.domain.product.dto.response.AuthResponse;
import com.sas.demo.domain.product.dto.response.UserResponse;
import com.sas.demo.domain.product.service.AuthService;
import com.sas.demo.domain.product.service.JwtService;
import com.sas.demo.domain.product.service.RefreshTokenService;
import com.sas.demo.domain.product.service.UserService;
import com.sas.demo.domain.repository.RoleRepository;
import com.sas.demo.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validate password confirmation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu xác nhận không khớp");
        }

        // Check if email already exists
        if (userService.existsByMail(request.getMail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Get default role (USER)
        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy role mặc định"));

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .mail(request.getMail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(defaultRole)
                .enabled(true)
                .accountLocked(false)
                .failLoginAttempts(0)
                .build();

        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        // Save refresh token
        refreshTokenService.createRefreshToken(savedUser, refreshToken);

        return buildAuthResponse(savedUser, accessToken, refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getMail(),
                            request.getPassword()
                    )
            );

            User user = (User) authentication.getPrincipal();

            // Reset fail login attempts on successful login
            user.setFailLoginAttempts(0);
            userRepository.save(user);

            // Update last login
            userService.updateLastLogin(user.getId());

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Save refresh token
            refreshTokenService.createRefreshToken(user, refreshToken);

            return buildAuthResponse(user, accessToken, refreshToken);

        } catch (AuthenticationException e) {
            // Handle failed login attempt
            userRepository.findByMail(request.getMail()).ifPresent(user -> {
                user.setFailLoginAttempts(user.getFailLoginAttempts() + 1);

                // Lock account after 5 failed attempts
                if (user.getFailLoginAttempts() >= 5) {
                    user.setAccountLocked(true);
                }
                userRepository.save(user);
            });

            throw new BadCredentialsException("Email hoặc mật khẩu không đúng");
        }
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        RefreshToken storedToken = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token không hợp lệ"));

        if (!refreshTokenService.isTokenValid(storedToken)) {
            throw new RuntimeException("Refresh token đã hết hạn hoặc bị thu hồi");
        }

        User user = storedToken.getUser();

        // Revoke old refresh token
        refreshTokenService.revokeToken(refreshToken);

        // Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Save new refresh token
        refreshTokenService.createRefreshToken(user, newRefreshToken);

        return buildAuthResponse(user, newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }

    @Override
    @Transactional
    public void logoutAll(String userId) {
        UUID uuid = UUID.fromString(userId);
        refreshTokenService.revokeAllUserTokens(uuid);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setMail(user.getMail());
        userResponse.setRole(user.getRole().getName());
        userResponse.setEnabled(user.getEnabled());
        userResponse.setLastLogin(user.getLastLogin());
        userResponse.setCreatedAt(user.getCreatedAt());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(userResponse)
                .build();
    }

}