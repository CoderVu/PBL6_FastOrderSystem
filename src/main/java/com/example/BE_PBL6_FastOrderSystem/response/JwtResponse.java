package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class JwtResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Double longitude;
    private Double latitude;
    private String avatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean accountLocked;
    private Boolean isActive;
    private String token;
    private String type = "Bearer";
    private List<String> roles;

    public JwtResponse(Long id, String email, String fullName, String phoneNumber, String address, Double longitude, Double latitude, String avatar, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean accountLocked, Boolean isActive, String token, List<String> roles) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.longitude = (longitude != null) ? longitude : null;
        this.latitude = (latitude != null) ? latitude : null;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.accountLocked = (accountLocked != null) ? accountLocked : null;
        this.isActive = (isActive != null) ? isActive : null;
        this.token = token;
        this.roles = roles;
    }


}
