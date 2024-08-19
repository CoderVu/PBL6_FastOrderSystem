package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.model.Order;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private Long orderId;
    private Long userId;
    private Long storeId;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private String status;
    private String paymentMethod;
    private String deliveryAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderDetailResponse> orderDetails;

    public OrderResponse(Order order) {
        this.orderId = order.getOrderId();
        this.userId = order.getUser().getId();
        this.storeId = order.getStore().getStoreId();
        this.orderDate = order.getOrderDate();
        this.totalAmount = order.getTotalAmount();
        this.status = order.getStatus();
        this.paymentMethod = order.getPaymentMethod().getName();
        this.deliveryAddress = order.getDeliveryAddress();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
        this.orderDetails = order.getOrderDetails().stream()
                .map(OrderDetailResponse::new)
                .collect(Collectors.toList());
    }
}