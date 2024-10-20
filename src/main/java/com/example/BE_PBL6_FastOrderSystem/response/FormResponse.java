package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;

@Data
public class FormResponse {
    private Long id;
    private String name;
    private String CitizenID;
    private String imageCitizenFront;
    private String imageCitizenBack;
    private String email;
    private String phone;
    private String address;
    private int age;
    private String vehicle;
    private String licensePlate;
    private String DriverLicense;
    public FormResponse(Long id, String name, String CitizenID, String imageCitizenFront, String imageCitizenBack, String email, String phone, String address, int age, String vehicle, String licensePlate, String DriverLicense) {
        this.id = id;
        this.name = name;
        this.CitizenID = CitizenID;
        this.imageCitizenFront = imageCitizenFront;
        this.imageCitizenBack = imageCitizenBack;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.age = age;
        this.vehicle = vehicle;
        this.licensePlate = licensePlate;
        this.DriverLicense = DriverLicense;
    }
}
