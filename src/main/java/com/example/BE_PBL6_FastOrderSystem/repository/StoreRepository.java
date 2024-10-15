package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository <Store, Long> {

    boolean existsByStoreName(String storeName);

    Optional<Store> findByStoreName(String storeName);
    @Query("SELECT s FROM Store s WHERE s.storeId = :storeId")
    Optional<Store> findByStoreId(Long storeId);
    @Query("SELECT s FROM Store s WHERE s.manager.id = :managerId")
    List<Store> findAllByManagerId(@Param("managerId") Long managerId);
    @Query("SELECT s FROM Store s WHERE s.manager.id = :managerId")
    boolean existsByManagerId(@Param("managerId") Long managerId);
    @Query("SELECT s FROM Store s WHERE s.manager.id = :managerId")
    Optional<Store> findByManagerId(Long managerId);
}
