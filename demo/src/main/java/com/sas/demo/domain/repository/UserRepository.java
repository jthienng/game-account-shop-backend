package com.sas.demo.domain.repository;

import com.sas.demo.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByUsername(String username);
    Optional<User> findByMail(String mail);
    boolean existsByMail(String mail);
}
