package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.exception.UserNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.request.LoginRequest;
import com.example.BE_PBL6_FastOrderSystem.request.UserRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IUserService {

    ResponseEntity<APIRespone> getUsers(String role);

    ResponseEntity<APIRespone> lockUserAccount(Long userId) throws UserNotFoundException;

    ResponseEntity<APIRespone> getUserProfile(Long userId);

    ResponseEntity<APIRespone> updateUser (Long id, UserRequest userRequest);

    ResponseEntity<APIRespone> unlockUserAccount(Long userId) throws UserNotFoundException;

    UserDetails loadUserByNumberPhone(String numberPhone);

    ResponseEntity<APIRespone> getAllUsers();

    ResponseEntity<APIRespone> addPhone(Long userId, String phone);
}