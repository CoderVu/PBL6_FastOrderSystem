package com.example.BE_PBL6_FastOrderSystem.respo.respository;

import com.example.BE_PBL6_FastOrderSystem.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(String role);
    Optional<Role> findByName(String role);
}
