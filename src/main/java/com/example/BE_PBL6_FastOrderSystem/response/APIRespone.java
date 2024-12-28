package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
public class APIRespone {
    private Boolean success;
    private String message;
    private Object data;
    public APIRespone(Boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
