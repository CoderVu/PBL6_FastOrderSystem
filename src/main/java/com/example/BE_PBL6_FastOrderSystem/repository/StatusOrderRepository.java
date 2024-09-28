package com.example.BE_PBL6_FastOrderSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.BE_PBL6_FastOrderSystem.model.StatusOrder;

public interface StatusOrderRepository extends JpaRepository<StatusOrder, Long> {
    StatusOrder findByStatusName(String statusName);
}