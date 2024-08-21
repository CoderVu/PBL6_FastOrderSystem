package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private Long amount;
    private String orderId;
    private Long cartId;
    private Long userId;
    private String deliveryAddress;
    private String orderInfo;
    private String lang;
    private String extraData;

}
