package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StaffRequest {
    private String employeeName;
    private String staff_code;
    private String department;
    private Long storeId;

    public StaffRequest(String employeeName, String staff_code,String department, Long storeId) {
        this.employeeName = employeeName;
        this.staff_code = staff_code;
        this.department = department;
        this.storeId = storeId;

    }
}
