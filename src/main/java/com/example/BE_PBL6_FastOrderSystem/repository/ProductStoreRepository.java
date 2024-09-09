package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.ProductStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductStoreRepository extends JpaRepository<ProductStore, Long> {
    @Query("SELECT ps FROM ProductStore ps WHERE ps.product.productId = :productId AND ps.store.storeId = :storeId")
    Optional<ProductStore> findByProductIdAndStoreId(Long productId, Long storeId);
}