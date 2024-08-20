package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderIdAndUserId(Long orderId, Long userId);
    List<Order> findAllByUserId(Long userId);

    boolean existsByOrderCode(String orderCode);
}