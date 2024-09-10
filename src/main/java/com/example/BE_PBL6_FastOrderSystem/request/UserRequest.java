package com.example.BE_PBL6_FastOrderSystem.request;

import org.springframework.web.multipart.MultipartFile;

public class UserRequest {
    private String fullName;
    private MultipartFile avatar;
    private String email;
    private String address;

    public UserRequest(String fullName, MultipartFile avatar, String email, String address) {
        this.fullName = fullName;
        this.avatar = avatar;
        this.email = email;
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }
    public MultipartFile getAvatar() {
        return avatar;
    }
    public void setAvatar(MultipartFile avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
}
