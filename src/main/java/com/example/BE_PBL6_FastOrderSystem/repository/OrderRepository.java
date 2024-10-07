package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.model.StatusOrder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    boolean existsByOrderCode(String orderCode);

    @Query("SELECT o FROM Order o WHERE o.orderCode = ?1")
    Optional<Order> findByOrderCode(String orderCode);

    @Query("SELECT o FROM Order o WHERE o.orderId = ?1")
    Optional<Order> findByOrderId(Long orderId);

    List<Order> findAllByStatusAndUserId(StatusOrder statusOrder, Long userId);

    @Query("SELECT o FROM Order o WHERE o.status = ?1")
    boolean findByStatusOrder(StatusOrder statusOrder);

    @Query("SELECT o FROM Order o WHERE o.user.id = ?1")
    Page<Order> findAllByUserId(Long userId, Pageable pageable);
    @Query("SELECT o FROM Order o JOIN o.orderDetails od WHERE od.store.storeId IN :storeIds")
    Page<Order> findAllByStoreIds(@Param("storeIds") List<Long> storeIds, Pageable pageable);
}
