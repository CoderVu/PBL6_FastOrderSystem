package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RateResponse {
    private Long rateId;
    private Long userId;
    private int rate;
    private String comment;
    private String createdAt;
    private String updatedAt;
    private Long productId;
    private Long comboId;

}
