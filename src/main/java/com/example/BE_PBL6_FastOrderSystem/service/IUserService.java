package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.exception.UserNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.User;

import java.util.List;

public interface IUserService {
    User registerUser(User user);

    User registerAdmin(User user);

    List<User> getUsers(String role);

    void lockUserAccount(Long userId) throws UserNotFoundException;
}