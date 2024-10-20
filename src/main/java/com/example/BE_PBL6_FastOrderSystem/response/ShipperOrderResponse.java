package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.entity.ShipperOrder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShipperOrderResponse {
    private Long shipperOrderId;
    private Long shipperId;
    private String status;
    private LocalDateTime receivedAt;
    private LocalDateTime deliveredAt;
    private Long storeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long orderId;
    private String orderCode;
    private Long userId;
    private String address;
    private String fullName;
    private String phone;
    private LocalDateTime orderDate;
    private Double totalAmount;
    private Double shippingFee;
    private String deliveryAddress;
    private Double longitude;
    private Double latitude;
    private List<OrderDetailResponse> orderDetails;


    public ShipperOrderResponse(ShipperOrder shipperOrder) {
        this.shipperOrderId = shipperOrder.getId();
        this.shipperId = shipperOrder.getShipper().getId();
        this.status = shipperOrder.getStatus();
        this.receivedAt = shipperOrder.getReceivedAt();
        this.deliveredAt = shipperOrder.getDeliveredAt();
        this.storeId = shipperOrder.getStore().getStoreId();
        this.createdAt = shipperOrder.getCreatedAt();
        this.updatedAt = shipperOrder.getUpdatedAt();
        this.orderId = shipperOrder.getOrderDetails().get(0).getOrder().getOrderId();
        this.orderCode = shipperOrder.getOrderDetails().get(0).getOrder().getOrderCode();
        this.userId = shipperOrder.getOrderDetails().get(0).getOrder().getUser().getId();
        this.address = shipperOrder.getOrderDetails().get(0).getOrder().getUser().getAddress();
        this.fullName = shipperOrder.getOrderDetails().get(0).getOrder().getUser().getFullName();
        this.phone = shipperOrder.getOrderDetails().get(0).getOrder().getUser().getPhoneNumber();
        this.orderDate = shipperOrder.getOrderDetails().get(0).getOrder().getOrderDate();
        this.totalAmount = shipperOrder.getOrderDetails().stream()
                .filter(orderDetail -> orderDetail.getStore().getStoreId().equals(this.storeId))
                .mapToDouble(orderDetail -> orderDetail.getTotalPrice())
                .sum(); // sum total price of order details of this store
        this.shippingFee = shipperOrder.getOrderDetails().get(0).getOrder().getShippingFee();
        this.deliveryAddress = shipperOrder.getOrderDetails().get(0).getOrder().getDeliveryAddress();
        this.longitude = shipperOrder.getOrderDetails().get(0).getOrder().getLongitude();
        this.latitude = shipperOrder.getOrderDetails().get(0).getOrder().getLatitude();
        this.orderDetails = shipperOrder.getOrderDetails().stream()
                .map(OrderDetailResponse::new)
                .collect(java.util.stream.Collectors.toList());

    }

}
