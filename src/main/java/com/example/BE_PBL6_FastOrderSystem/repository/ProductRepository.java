package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory_CategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.productName LIKE %?1%")
    List<Product> findByProductNameContaining(String productName);

    List<Product> findByBestSale(boolean bestSale);

    boolean existsByProductName(String productName);

    @Query("SELECT p FROM Product p JOIN p.combos c WHERE c.comboId = :comboId")
    List<Product> findByComboId(@Param("comboId") Long comboId);
    @Query("SELECT p FROM Product p JOIN p.productStores ps JOIN ps.store s WHERE s.storeId = :storeId")
    List<Product> findByStoreId(@Param("storeId") Long storeId);
    Optional<Product> findByProductId(Long productId);
    

}