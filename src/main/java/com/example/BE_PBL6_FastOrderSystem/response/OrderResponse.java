package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.entity.Order;
import com.example.BE_PBL6_FastOrderSystem.entity.Payment;
import com.example.BE_PBL6_FastOrderSystem.repository.PaymentRepository;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private Long orderId;
    private Long shipperId;
    private Long storeId;
    private String orderCode;
    private Long userId;
    private LocalDateTime orderDate;
    private String paymentMethod;
    private String statusPayment;
    private Double totalAmount;
    private Double shippingFee;
    private String status;
    private String deliveryAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String discountCode;
    private boolean isFeedBack;
    private Double latitude;
    private Double longitude;
    private List<OrderDetailResponse> orderDetails;

    public OrderResponse(Order order, String paymentMethod, String statusPayment) {
        this.orderId = order.getOrderId();
        this.longitude = order.getLongitude();
        this.latitude = order.getLatitude();
        this.shipperId = (!order.getOrderDetails().isEmpty() && order.getOrderDetails().get(0).getShipperOrder() != null) ? order.getOrderDetails().get(0).getShipperOrder().getShipper().getId() : 0;
        this.storeId = order.getOrderDetails().get(0).getStore().getStoreId();
        this.orderCode = order.getOrderCode();
        this.userId = order.getUser().getId();
        this.orderDate = order.getOrderDate();
        this.totalAmount = order.getTotalAmount();
        this.shippingFee = (!order.getOrderDetails().isEmpty() && order.getOrderDetails().get(0).getShippingFee() != null) ? order.getOrderDetails().get(0).getShippingFee().getFee() : 0.0;
        this.paymentMethod = paymentMethod;
        this.statusPayment = statusPayment;
        this.status = (order.getStatus() != null) ? order.getStatus().getStatusName() : "Unknown Status";
        this.deliveryAddress = order.getDeliveryAddress();
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
        this.discountCode = (order.getVoucher() != null) ? order.getVoucher().getCode() : "Unknown Discount Code";
        this.isFeedBack = order.getFeedback();
        this.orderDetails = order.getOrderDetails().stream().map(OrderDetailResponse::new).collect(Collectors.toList());
    }
}