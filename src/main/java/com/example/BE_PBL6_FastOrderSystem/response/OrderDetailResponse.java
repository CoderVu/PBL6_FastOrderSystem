package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.model.OrderDetail;
import lombok.Data;

@Data
public class OrderDetailResponse {
    private Long orderDetailId;
    private Long productId;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;

    public OrderDetailResponse(OrderDetail orderDetail) {
        this.orderDetailId = orderDetail.getOrderDetailId();
        this.productId = orderDetail.getProduct().getProductId();
        this.quantity = orderDetail.getQuantity();
        this.unitPrice = orderDetail.getUnitPrice();
        this.totalPrice = orderDetail.getTotalPrice();
    }
}