package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findByEmail(String email);
}
