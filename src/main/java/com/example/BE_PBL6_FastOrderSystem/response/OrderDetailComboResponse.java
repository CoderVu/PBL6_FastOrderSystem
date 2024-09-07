package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.model.OrderDetail;
import lombok.Data;

@Data
public class OrderDetailComboResponse {
    private Long orderDetailId;
    private ComboResponse combo;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private String size;

    public OrderDetailComboResponse(OrderDetail orderDetail) {
        this.orderDetailId = orderDetail.getOrderDetailId();
        this.combo = new ComboResponse(orderDetail.getCombo());
        this.quantity = orderDetail.getQuantity();
        this.unitPrice = orderDetail.getUnitPrice();
        this.totalPrice = orderDetail.getTotalPrice();
        this.size = orderDetail.getSize().getName();
    }
}