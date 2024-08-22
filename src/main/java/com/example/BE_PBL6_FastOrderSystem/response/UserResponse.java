package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class UserResponse {
    private Long id;
    private String phoneNumber;
    private String fullName;
    private String avatar;
    private String email;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean accountLocked;
    private RoleResponse role;  // Change from List<RoleResponse> to a single RoleResponse


    public UserResponse(Long id, String phoneNumber, String fullName, String avatar, String email, String address, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean accountLocked, RoleResponse role, List<StoreResponse> stores, List<OrderResponse> orders) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.avatar = avatar;
        this.email = email;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.accountLocked = accountLocked;
        this.role = role;
    }

    public UserResponse(User user) {
        this.id = user.getId();
        this.phoneNumber = user.getPhoneNumber();
        this.fullName = user.getFullName();
        this.avatar = user.getAvatar();
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.accountLocked = user.isAccountLocked();
        this.role = new RoleResponse(user.getRole().getId(), user.getRole().getName());  // Use the single role
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public Boolean getAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(Boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public RoleResponse getRole() {  // Change from getRoles() to getRole()
        return role;
    }

    public void setRole(RoleResponse role) {  // Change from setRoles() to setRole()
        this.role = role;
    }

}
