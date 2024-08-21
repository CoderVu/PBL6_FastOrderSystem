package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallbackRequestDTO {

    private Long amount;
    private String orderId;
    private String orderInfo;

}
