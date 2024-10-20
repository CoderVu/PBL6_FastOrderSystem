package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.entity.OrderDetail;
import com.example.BE_PBL6_FastOrderSystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderDetailRepository  extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findOrderDetailsByProductIn(List<Product> products);
    @Query("SELECT od FROM OrderDetail od WHERE od.order.orderId = ?1")
    List<OrderDetail> findByOrderId(Long orderId);
    @Query("SELECT od FROM OrderDetail od WHERE od.store.manager.id = ?1")
    List<OrderDetail> findByStoreManagerId(Long ownerId);

    @Query("SELECT od FROM OrderDetail od WHERE od.status.statusName = ?1")
    List<OrderDetail> findAllByStatus(String status);
    @Query("SELECT od FROM OrderDetail od WHERE od.order.orderCode = ?1 and od.store.storeId = ?2")

    List<OrderDetail> findByOrder_OrderCode_AndStoreId(String orderCode, Long storeId);
}
