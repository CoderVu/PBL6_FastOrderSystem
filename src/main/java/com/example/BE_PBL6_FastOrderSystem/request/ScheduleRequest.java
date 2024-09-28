package com.example.BE_PBL6_FastOrderSystem.request;

import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleRequest {
    private String shift;
    private LocalDateTime startShift;
    private LocalDateTime endShift;
    private LocalDateTime date;
    private Long staffId;
    public ScheduleRequest(String shift, LocalDateTime startShift, LocalDateTime endShift, LocalDateTime date, Long staffId) {
        this.shift = shift;
        this.startShift = startShift;
        this.endShift = endShift;
        this.date = date;
        this.staffId = staffId;
    }
}
