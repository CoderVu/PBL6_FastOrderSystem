package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.entity.Product;
import com.example.BE_PBL6_FastOrderSystem.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    boolean existsByName(String promotionName);

    boolean existsByStores_StoreId(Long storeId);

    List<Promotion> findPromotionsByProductsIn(List<Product> products);
}