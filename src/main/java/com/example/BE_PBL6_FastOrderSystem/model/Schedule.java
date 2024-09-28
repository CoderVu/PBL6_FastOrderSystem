package com.example.BE_PBL6_FastOrderSystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String shift;
    private LocalDateTime startShift;
    private LocalDateTime endShift;
    private LocalDateTime date;
    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;
    @Override
    public String toString() {
        return "Schedule{" +
                "id=" + id +
                ", shift='" + shift + '\'' +
                ", startShift=" + startShift +
                ", endShift=" + endShift +
                ", date=" + date +
                ", staff=" + staff +
                '}';
    }
}