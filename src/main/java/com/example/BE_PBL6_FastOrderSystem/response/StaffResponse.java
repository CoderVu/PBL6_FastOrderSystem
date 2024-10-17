package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

@Data
public class StaffResponse {
    private Long id;
    private String employeeName;
    private String staff_code;
    private String department;
    private Long storeId;

    public StaffResponse(Long id, String employeeName,String staff_code, String department, Long storeId) {
        this.id = id;
        this.employeeName = employeeName;
        this.staff_code = staff_code;
        this.department = department;
        this.storeId = storeId;;
    }


}
