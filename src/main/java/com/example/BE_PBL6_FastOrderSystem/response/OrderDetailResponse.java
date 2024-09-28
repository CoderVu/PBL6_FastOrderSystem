package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.model.OrderDetail;
import lombok.Data;

@Data
public class OrderDetailResponse {
    private String type;
    private OrderDetailProductResponse productDetail;
    private OrderDetailComboResponse comboDetail;

    public OrderDetailResponse(OrderDetail orderDetail) {
        if (orderDetail.getCombo() != null) {
            this.type = "combo";
            this.comboDetail = new OrderDetailComboResponse(orderDetail);
        } else if (orderDetail.getProduct() != null) {
            this.type = "product";
            this.productDetail = new OrderDetailProductResponse(orderDetail);
        }
    }
}