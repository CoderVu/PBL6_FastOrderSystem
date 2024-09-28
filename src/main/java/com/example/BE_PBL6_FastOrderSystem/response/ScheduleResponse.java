package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ScheduleResponse {
    private Long id;
    private String shift;
    private LocalDateTime startShift;
    private LocalDateTime endShift;
    private LocalDateTime date;
    private Long staffId;


    public ScheduleResponse(Long id, String shift, LocalDateTime startShift, LocalDateTime endShift, LocalDateTime date, Long staffId) {
        this.id = id;
        this.shift = shift;
        this.startShift = startShift;
        this.endShift = endShift;
        this.date = date;
        this.staffId = staffId;
    }
}
