package com.sas.demo.domain.repo;

import com.sas.demo.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByMail(String mail);
    boolean existsByMail(String mail);
    boolean existsByUsername(String username);
}