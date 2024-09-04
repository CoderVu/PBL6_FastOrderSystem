package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.Combo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComboRepository extends JpaRepository <Combo, Long> {

    Optional<Object> findByComboName(String comboName);

    boolean existsByComboName(String comboName);
}
