package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.model.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
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
    private RoleResponse role;


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
        this.role = new RoleResponse(user.getRole().getId(), user.getRole().getName());
    }

}
