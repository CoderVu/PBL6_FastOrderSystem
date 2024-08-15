package com.example.BE_PBL6_FastOrderSystem.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class StoreResponse {
    private Long storeId;
    private String storeName;
    private String location;
    @JsonIgnore
    private UserRespose user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Constructor cũ
    public StoreResponse(Long storeId, String storeName, String location, UserRespose user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.location = location;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    // Constructor mới
    public StoreResponse(Long storeId, String storeName, String location,  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.location = location;
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
}
