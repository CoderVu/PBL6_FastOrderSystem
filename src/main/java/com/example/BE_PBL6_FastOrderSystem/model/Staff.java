package com.example.BE_PBL6_FastOrderSystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String employeeName;
    private String staff_code;
    private String department;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    @Override
    public String toString() {
        return "Staff{" +
                "id=" + id +
                ", employeeName='" + employeeName + '\'' +
                ", staff_code='" + staff_code + '\'' +
                ", department='" + department + '\'' +
                ", store=" + store +
                '}';
    }
}
