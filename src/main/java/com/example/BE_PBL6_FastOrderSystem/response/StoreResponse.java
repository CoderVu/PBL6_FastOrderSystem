package com.example.BE_PBL6_FastOrderSystem.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class StoreResponse {
    private Long storeId;
    private String storeName;
    private String location;
    private Double longitude;
    private Double latitude;
    private  LocalDateTime openingTime;
    private  LocalDateTime closingTime;
    @JsonIgnore
    private UserRespose user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Constructor cũ
    public StoreResponse(Long storeId, String storeName, String location, Double longitude, Double latitude, LocalDateTime openingTime, LocalDateTime closingTime, UserRespose user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    // Constructor mới
    public StoreResponse(Long storeId, String storeName, String location, Double longitude, Double latitude, LocalDateTime openingTime, LocalDateTime closingTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.location = location;
        this.longitude = longitude != null ? longitude : 0.0;
        this.latitude = latitude != null ? latitude : 0.0;
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

    public UserRespose getUser() {
        return user;
    }

    public void setUser(UserRespose user) {
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

    public LocalDateTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalDateTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalDateTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalDateTime closingTime) {
        this.closingTime = closingTime;
    }
}
