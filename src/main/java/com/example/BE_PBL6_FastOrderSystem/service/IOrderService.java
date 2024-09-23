package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.Cart;
import com.example.BE_PBL6_FastOrderSystem.model.Order;
import com.example.BE_PBL6_FastOrderSystem.model.OrderDetail;
import com.example.BE_PBL6_FastOrderSystem.model.Size;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IOrderService {
    String generateUniqueOrderCode();

    ResponseEntity<APIRespone> processOrder(Long userId, String paymentMethod, List<Long> cartIds, String deliveryAddress, String orderCode);

    ResponseEntity<APIRespone> processOrderNow(Long userId, String paymentMethod, Long productId, Long comboId, Long drinkId, Long storeId, Integer quantity, String size, String deliveryAddress, String orderCode);
    Long calculateOrderNowAmount(Long productId, Long comboId, int quantity);
    ResponseEntity<APIRespone> updateQuantityProduct(Long productId, Long comboId, Long storeId, int quantity);
    ResponseEntity<APIRespone> updateOrderStatus(String orderCode, String status);

    ResponseEntity<APIRespone> getAllOrderDetailOfStore(Long storeId);

    ResponseEntity<APIRespone> getOrderDetailOfStore(Long ownerId, String orderCode);

    ResponseEntity<APIRespone> getOrderDetailByUserId(Long userId, String orderCode);

    ResponseEntity<APIRespone> updateStatusDetail(String orderCode, Long storeId, String Status);
    ResponseEntity<APIRespone> cancelOrder(String orderCode, Long serId);
    List<Cart> getCartItemsByCartId(Long cartId);
    ResponseEntity<APIRespone> findOrderByOrderCode(String orderCode);
    ResponseEntity<APIRespone> getOrdersByStatusAndUserId(String status, Long userId);
    ResponseEntity<APIRespone>  findOrderByOrderCodeAndUserId(String orderCode, Long userId);
    ResponseEntity<APIRespone> getAllOrderDetailsByUser(Long userId);
}