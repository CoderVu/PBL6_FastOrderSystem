package com.example.BE_PBL6_FastOrderSystem.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
@Data
public class StoreRequest {
    private String storeName;
    private MultipartFile image;
    private String phoneNumber;
    private String location;
    private Double longitude;
    private Double latitude;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    private Long managerId;
    public StoreRequest(String storeName, MultipartFile image, String phoneNumber, String location, Double longitude, Double latitude, LocalDateTime openingTime, LocalDateTime closingTime, Long managerId) {
        this.storeName = storeName;
        this.image = image;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.managerId = managerId;
    }

}
