package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.OrderDetail;
import com.example.BE_PBL6_FastOrderSystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderDetailRepository  extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findOrderDetailsByProductIn(List<Product> products);
    @Query("SELECT od FROM OrderDetail od WHERE od.order.orderId = ?1")
    List<OrderDetail> findByOrderId(Long orderId);
    @Query("SELECT od FROM OrderDetail od WHERE od.store.manager.id = ?1")
    Optional<OrderDetail> findByStoreManagerId(Long ownerId);

    @Query("SELECT od FROM OrderDetail od WHERE od.status.statusName = ?1")
    List<OrderDetail> findAllByStatus(String status);
}
