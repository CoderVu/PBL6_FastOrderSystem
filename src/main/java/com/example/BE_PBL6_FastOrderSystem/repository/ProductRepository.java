package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStore_StoreId(Long storeId);

    List<Product> findByCategory_CategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.productName LIKE %?1%")
    List<Product> findByProductNameContaining(String productName);

    List<Product> findByBestSale(boolean bestSale);

    boolean existsByProductName(String productName);

}