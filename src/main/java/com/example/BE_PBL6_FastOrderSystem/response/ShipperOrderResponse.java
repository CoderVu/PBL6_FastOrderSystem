package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.model.ShipperOrder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShipperOrderResponse {
    private OrderResponse order;
    private Long shipperId;
    private String status;
    private LocalDateTime deliveryTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ShipperOrderResponse(ShipperOrder shipperOrder) {
        this.order = new OrderResponse(shipperOrder.getOrderDetails().get(0).getOrder());
        this.shipperId = shipperOrder.getShipper().getId();
        this.status = shipperOrder.getDeliveryStatus();
        this.createdAt = shipperOrder.getCreatedAt();
        this.updatedAt = shipperOrder.getUpdatedAt();
    }
}
