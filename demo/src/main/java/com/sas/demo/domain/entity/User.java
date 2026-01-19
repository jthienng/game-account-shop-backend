package com.sas.demo.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "uuid", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "username", nullable = false, length = 255, unique = true)
    private String username;

    @Size(max = 255)
    @NotNull
    @Column(name = "mail", nullable = false, length = 255, unique = true)
    private String mail;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "enabled")
    @ColumnDefault("true")
    private Boolean enabled = true;

    @Column(name = "account_locked")
    @ColumnDefault("false")
    private Boolean accountLocked = false;

    @Column(name = "fail_login_attempts")
    @ColumnDefault("0")
    private Integer failLoginAttempts = 0;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Default của interface, account không expire
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Default, credentials không expire
    }

    @Override
    public boolean isEnabled() {
        return enabled;  // Dùng field từ DB, quan trọng nhất!
    }
}