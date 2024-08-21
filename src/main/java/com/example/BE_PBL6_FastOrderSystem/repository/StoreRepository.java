package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository <Store, Long> {

    boolean existsByStoreName(String storeName);

    Optional<Store> findByStoreName(String storeName);
}
