package com.example.BE_PBL6_FastOrderSystem.response;
import lombok.Data;

@Data
public class StaffResponse {
    private Long id;
    private String employeeName;
    private String staff_code;
    private String department;
    private String storeName;

    public StaffResponse(Long id, String employeeName,String staff_code, String department, String storeName) {
        this.id = id;
        this.employeeName = employeeName;
        this.staff_code = staff_code;
        this.department = department;
        this.storeName = storeName;
    }

}
