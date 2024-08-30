package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.request.RefreshRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.JwtResponse;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<APIRespone> authenticateUser(String numberPhone, String password);

    ResponseEntity<APIRespone> registerUser(User user);

    ResponseEntity<APIRespone> registerAdmin(User user);


    void logout(String token);

    boolean isTokenInvalid(String token);

    void invalidateToken(String refreshToken);

    ResponseEntity<APIRespone> SendOTP(String email);

    ResponseEntity<APIRespone> confirmOTP(String email, String otp, String newPassword);
}
