package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.CartItem;
import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOrderService {
    String generateUniqueOrderCode();

    ResponseEntity<APIRespone> placeOrder(Long UserId ,String paymentMethod, List<Long> cartIds, String deliveryAddress, String orderCode);

    ResponseEntity<APIRespone> updateOrderStatusOfOwner(Long orderId, Long ownerId, String status);

    ResponseEntity<APIRespone> updateOrderStatus(String orderCode, String status);

    ResponseEntity<APIRespone> getOrderByIdAndUserId(Long orderId, Long userId);
    ResponseEntity<APIRespone> getAllOrdersByUser(Long userId);

    List<CartItem> getCartItemsByCartId(Long cartId);

    ResponseEntity<APIRespone> getStatusOrder(Long orderId, Long userId);


    Order findOrderByOrderCode(String orderCode);

    Order findOrderByOrderIdAndOwnerId(Long orderId, Long ownerId);

    ResponseEntity<APIRespone> getAllOrdersByOwner(Long ownerId);
}