package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.exception.UserNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.request.LoginRequest;
import com.example.BE_PBL6_FastOrderSystem.request.UserRequest;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;

import java.util.List;

public interface IUserService {

    List<User> getUsers(String role);

    void lockUserAccount(Long userId) throws UserNotFoundException;

    User getUserProfile(String userId);

    UserResponse updateUser (Long id, UserRequest userRequest);

    void unlockUserAccount(Long userId) throws UserNotFoundException;
}