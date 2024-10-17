package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Long amount;
    private String orderId;
    private List<Long> cartIds;
    private Long productId;
    private Long storeId;
    private Integer quantity;
    private Long comboId;
    private Long drinkId;
    private String size;
    private String appuser;
    private String apptransid;
    private Long order_id;
    private Long userId;
    private String deliveryAddress;
    private Double longitude;
    private Double latitude;
    private String orderInfo;
    private String lang;
    private String extraData;
    private String paymentMethod;
}