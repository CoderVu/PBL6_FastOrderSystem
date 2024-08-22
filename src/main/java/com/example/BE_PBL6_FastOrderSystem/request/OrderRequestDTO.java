package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    private Long amount;
    private String orderCode;
    private List<Long> cartIds;
    private Long userId;
    private String deliveryAddress;
    private String orderInfo;
    private String lang;
    private String extraData;
    private String paymentMethod;
}