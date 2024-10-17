package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.entity.OrderDetail;
import lombok.Data;

@Data
public class OrderDetailComboResponse {
    private Long orderDetailId;
    private Long comboId;
    private String comboName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private String size;
    private String drinkId;
    private Long storeId;
    private String status;

    public OrderDetailComboResponse(OrderDetail orderDetail) {
        this.orderDetailId = orderDetail.getOrderDetailId();
        this.comboId = orderDetail.getCombo().getComboId();
        this.comboName = orderDetail.getCombo().getComboName();
        this.quantity = orderDetail.getQuantity();
        this.unitPrice = orderDetail.getUnitPrice();
        this.totalPrice = orderDetail.getTotalPrice();
        this.size = orderDetail.getSize().getName();
        this.drinkId = String.valueOf(orderDetail.getDrinkProduct() != null ? orderDetail.getDrinkProduct().getProductId() : null);
        this.storeId = orderDetail.getStore() != null ? orderDetail.getStore().getStoreId() : null;
        this.status = orderDetail.getStatus().getStatusName();
    }
}