package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, Long> {

    @Query("SELECT r FROM Rate r WHERE r.product.productId = ?1")
    List<Rate> findByProductId(Long productId);
    @Query("SELECT r FROM Rate r WHERE r.userId = ?1 AND r.product.productId = ?2")
    Optional<Rate> findByUserIdAndProductId(Long userId, Long productId);
    @Query("SELECT r FROM Rate r WHERE r.userId = ?1 AND r.combo.comboId = ?2")
    Optional<Rate> findByUserIdAndComboId(Long userId, Long comboId);
    @Query("SELECT r FROM Rate r WHERE r.combo.comboId = ?1")
    List<Rate> findByComboId(Long comboId);
}
