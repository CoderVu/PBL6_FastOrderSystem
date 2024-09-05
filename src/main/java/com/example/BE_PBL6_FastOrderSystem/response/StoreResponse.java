package com.example.BE_PBL6_FastOrderSystem.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
@Data
public class StoreResponse {
    private Long storeId;
    private String storeName;
    private String image;
    private String location;
    private Double longitude;
    private Double latitude;
    private String numberPhone;
    private Date openingTime;
    private  Date closingTime;
    @JsonIgnore
    private UserResponse user;
    private String managerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Constructor cũ
    public StoreResponse(Long storeId, String storeName,String image ,String location, Double longitude, Double latitude, String numberPhone, Date openingTime, Date closingTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.image = image;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.numberPhone = numberPhone;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    // Constructor mới
    public StoreResponse(Long storeId, String storeName, String image, String location, Double longitude, Double latitude,String numberPhone, Date openingTime, Date closingTime, String managerName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.image = image;
        this.location = location;
        this.longitude = longitude != null ? longitude : 0.0;
        this.latitude = latitude != null ? latitude : 0.0;
        this.numberPhone = numberPhone;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.managerName = managerName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


}
