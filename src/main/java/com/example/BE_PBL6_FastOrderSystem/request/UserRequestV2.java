package com.example.BE_PBL6_FastOrderSystem.request;

import org.springframework.web.multipart.MultipartFile;

public class UserRequestV2 {
    private String fullName;
    private String avatar;
    private String email;
    private String address;

    public UserRequestV2(String fullName, String avatar, String email, String address) {
        this.fullName = fullName;
        this.avatar = avatar;
        this.email = email;
        this.address = address;
    }

    public String getFullName() {
        return fullName;
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

    public String getAddress() {
        return address;
    }
}
