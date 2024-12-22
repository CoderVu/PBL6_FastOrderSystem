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
    private Long managerId;
    private Double longitude;
    private Double latitude;
    private String numberPhone;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer stockQuantity;
    // Constructor cho việc lấy thông tin cơ bản của cửa hàng và số lượng hàng tồn kho của product hiện tại
    public StoreResponse(Long storeId, String storeName,String image ,String location, Double longitude, Double latitude, String numberPhone, LocalTime openingTime, LocalTime closingTime, LocalDateTime createdAt, LocalDateTime updatedAt, Integer stockQuantity) {
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
        this.stockQuantity = stockQuantity;
    }
    // Constructor cho việc lấy thông tin cơ bản của cửa hàng và thông tin của người quản lý
    public StoreResponse(Long storeId, String storeName, String image, String location, Double longitude, Double latitude,String numberPhone,LocalTime openingTime,LocalTime closingTime, LocalDateTime createdAt, LocalDateTime updatedAt,Long managerId) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.image = image;
        this.managerId = managerId;
        this.location = location;
        this.longitude = longitude != null ? longitude : 0.0;
        this.latitude = latitude != null ? latitude : 0.0;
        this.numberPhone = numberPhone;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
