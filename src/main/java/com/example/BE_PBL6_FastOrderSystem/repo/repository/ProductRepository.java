package com.example.BE_PBL6_FastOrderSystem.repo.repository;

import com.example.BE_PBL6_FastOrderSystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStore_StoreId(Long storeId);
}
