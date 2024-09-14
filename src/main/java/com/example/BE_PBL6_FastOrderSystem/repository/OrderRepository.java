package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.model.StatusOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user.id = ?1")
    List<Order> findAllByUserId(Long userId);
    boolean existsByOrderCode(String orderCode);
    @Query("SELECT o FROM Order o WHERE o.orderCode = ?1")
    Optional<Order> findByOrderCode(String orderCode);
    @Query("SELECT o FROM Order o WHERE o.orderId = ?1")
    Optional<Order> findByOrderId(Long orderId);
    List<Order> findAllByStatusAndUserId(StatusOrder statusOrder, Long userId);
}