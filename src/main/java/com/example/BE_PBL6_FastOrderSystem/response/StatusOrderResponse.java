package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

@Data
public class StatusOrderResponse {
    private Long statusId;
    private String statusName;
    public StatusOrderResponse(Long statusId, String statusName) {
        this.statusId = statusId;
        this.statusName = statusName;
    }

}
