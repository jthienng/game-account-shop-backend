package com.sas.demo.domain.repo;

import com.sas.demo.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepo extends JpaRepository<Role,Integer> {
    Optional<Role> findByName(String name);
}
