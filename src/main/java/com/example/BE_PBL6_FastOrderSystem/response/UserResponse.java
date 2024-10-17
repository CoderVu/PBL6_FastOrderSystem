package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String phoneNumber;
    private String fullName;
    private String avatar;
    private String email;
    private String address;
    private Double longitude;
    private Double latitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean accountLocked;
    private Boolean isActive;
    private Boolean isApproved;
    private RoleResponse role;


    public UserResponse(User user) {
        this.id = user.getId();
        this.phoneNumber = user.getPhoneNumber();
        this.fullName = user.getFullName();
        this.avatar = user.getAvatar();
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.longitude = (user != null) ? user.getLongitude() : null;
        this.latitude = (user != null) ? user.getLatitude() : null;
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.accountLocked = user.getAccountLocked();
        this.isActive = (user != null) ? user.getIsActive() : null;
        this.isApproved = (user != null) ? user.getIsApproved() : null;
        this.role = new RoleResponse(user.getRole().getId(), user.getRole().getName());
    }


}
