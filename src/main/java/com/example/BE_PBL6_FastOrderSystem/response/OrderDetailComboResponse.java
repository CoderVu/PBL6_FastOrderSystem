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
    private String drinkId;
    private Long storeId;
    private String status;

    public OrderDetailComboResponse(OrderDetail orderDetail) {
        this.orderDetailId = orderDetail.getOrderDetailId();
        this.combo = ResponseConverter.convertToComboResponse(orderDetail.getCombo());
        this.quantity = orderDetail.getQuantity();
        this.unitPrice = orderDetail.getUnitPrice();
        this.totalPrice = orderDetail.getTotalPrice();
        this.size = orderDetail.getSize().getName();
        this.drinkId = String.valueOf(orderDetail.getDrinkProduct() != null ? orderDetail.getDrinkProduct().getProductId() : null);
        this.storeId = orderDetail.getStore() != null ? orderDetail.getStore().getStoreId() : null;
        this.status = orderDetail.getStatus().getStatusName();
    }
}