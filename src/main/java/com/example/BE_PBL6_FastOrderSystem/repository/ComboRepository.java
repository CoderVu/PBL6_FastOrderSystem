package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ComboRepository extends JpaRepository <Combo, Long> {

    Optional<Object> findByComboName(String comboName);

    boolean existsByComboName(String comboName);

    @Query("SELECT DISTINCT c FROM Combo c JOIN c.products p JOIN p.productStores ps WHERE ps.store.storeId = :storeId")
    List<Combo> findCombosByStoreId(@Param("storeId") Long storeId);
}
