package com.example.BE_PBL6_FastOrderSystem.request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserRequestV2 {
    private String fullName;
    private String avatar;
    private String email;
    private String address;
    private Double latitude;
    private Double longitude;

}

