package com.example.BE_PBL6_FastOrderSystem.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class StoreResponse {
    private Long storeId;
    private String storeName;
    private String location;
    private Double longitude;
    private Double latitude;
    private String numberPhone;
    private Date openingTime;
    private  Date closingTime;
    @JsonIgnore
    private UserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Constructor cũ
    public StoreResponse(Long storeId, String storeName, String location, Double longitude, Double latitude, String numberPhone, Date openingTime, Date closingTime, UserResponse user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.numberPhone = numberPhone;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    // Constructor mới
    public StoreResponse(Long storeId, String storeName, String location, Double longitude, Double latitude,String numberPhone, Date openingTime, Date closingTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.location = location;
        this.longitude = longitude != null ? longitude : 0.0;
        this.latitude = latitude != null ? latitude : 0.0;
        this.numberPhone = numberPhone;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UserResponse getUser() {
        return user;
    }
    public void setUser(UserResponse user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public String getNumberPhone() {
        return numberPhone;
    }
    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public Date getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(Date openingTime) {
        this.openingTime = openingTime;
    }

    public Date getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(Date closingTime) {
        this.closingTime = closingTime;
    }
}
