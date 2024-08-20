package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse placeOrder(Long userId, String paymentMethod, Long cartId);

    String updateOrderStatus(Long orderId, Long ownerId, String status);

    OrderResponse getOrderByIdAndUserId(Long orderId, Long userId);
    List<OrderResponse> getAllOrdersByUser(Long userId);
}