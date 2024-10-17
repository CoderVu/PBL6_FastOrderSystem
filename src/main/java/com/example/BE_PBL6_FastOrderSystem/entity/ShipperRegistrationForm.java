package com.example.BE_PBL6_FastOrderSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class ShipperRegistrationForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @Column(name="name")
    private String name;
    @Column(name="CitizenID")
    private String  CitizenID;
    @Column(name = "imageCitizenFront", columnDefinition = "LONGTEXT")
    private String imageCitizenFront;
    @Column(name = "imageCitizenBack", columnDefinition = "LONGTEXT")
    private String imageCitizenBack;
    @Column(name="email")
    private String email;
    @Column(name="phone")
    private String phone;
    @Column(name="address")
    private String address;
    @Column(name="age")
    private int age;
    @Column(name="vehicle")
    private String vehicle;
    @Column(name="licensePlate")
    private String licensePlate;
    @Column(name="DriverLicense")
    private String DriverLicense;
    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
