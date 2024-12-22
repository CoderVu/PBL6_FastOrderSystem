package com.example.BE_PBL6_FastOrderSystem.request;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@Data
public class UserRequestV2 {
    private String fullName;
    private String avatar;
    private String email;
    private String address;
    private Double latitude;
    private Double longitude;


    public UserRequestV2(String fullName, String avatar, String email, String address) {
        this.fullName = fullName;
        this.avatar = avatar;
        this.email = email;
        this.address = address;
    }
    public UserRequestV2(String fullName, String avatar, String email, String address, Double latitude, Double longitude) {
        this.fullName = fullName;
        this.avatar = avatar;
        this.email = email;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

