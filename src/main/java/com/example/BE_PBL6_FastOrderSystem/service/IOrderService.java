package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.CartItem;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOrderService {
    String generateUniqueOrderCode();

    ResponseEntity<APIRespone> placeOrder(Long UserId ,String paymentMethod, List<Long> cartIds, String deliveryAddress);

    ResponseEntity<APIRespone> updateOrderStatus(Long orderId, Long ownerId, String status);

    ResponseEntity<APIRespone> getOrderByIdAndUserId(Long orderId, Long userId);
    ResponseEntity<APIRespone> getAllOrdersByUser(Long userId);

    List<CartItem> getCartItemsByCartId(Long cartId);
}