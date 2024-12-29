package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

import java.util.List;
@Data
public class ApplyVoucherRequest {
    private List<Long> voucherIds;
    private Long storeId;
}
