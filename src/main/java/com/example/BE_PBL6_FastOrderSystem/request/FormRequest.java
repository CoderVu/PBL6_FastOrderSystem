package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FormRequest {
    private String name;
    private int CitizenID;
    private MultipartFile imageCitizenFront;
    private MultipartFile imageCitizenBack;
    private String email;
    private String phone;
    private String address;
    private int age;
    private String vehicle;
    private String licensePlate;
    private String DriverLicense;
    public FormRequest(String name, int CitizenID, MultipartFile imageCitizenFront, MultipartFile imageCitizenBack, String email, String phone, String address, int age, String vehicle, String licensePlate, String DriverLicense) {
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
