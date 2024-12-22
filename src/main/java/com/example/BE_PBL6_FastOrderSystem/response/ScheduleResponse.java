package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.entity.Staff;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ScheduleResponse {
    private Long id;
    private String shift;
    private LocalDateTime startShift;
    private LocalDateTime endShift;
    private LocalDateTime date;
    private StaffResponse staffResponse;


    public ScheduleResponse(Long id, String shift, LocalDateTime startShift, LocalDateTime endShift, LocalDateTime date, Staff staff) {
        this.id = id;
        this.shift = shift;
        this.startShift = startShift;
        this.endShift = endShift;
        this.date = date;
        StaffResponse response = new StaffResponse(staff.getId(),staff.getEmployeeName(),staff.getStaff_code(),staff.getDepartment(),staff.getStore().getStoreName());
        this.staffResponse = response;
    }
}
