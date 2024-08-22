package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.request.UserRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetailsService;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user/auth")
public class UserAuthController {
    private final IUserService userService;

    @GetMapping("/profile")
    public ResponseEntity<APIRespone> getUserProfile() {
        Long userId = FoodUserDetails.getCurrentUserId();
       return userService.getUserProfile(userId);
    }

    @PutMapping("/profile/update")
    public ResponseEntity<APIRespone> updateUser(
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("password") String password,
            @RequestParam("fullName") String fullName,
            @RequestParam("avatar") MultipartFile avatar,
            @RequestParam("email") String email,
            @RequestParam("address") String address) {

             Long userId = FoodUserDetails.getCurrentUserId();
             UserRequest userResquest = new UserRequest(phoneNumber, password, fullName, avatar, email, address);
             return userService.updateUser(userId, userResquest);

    }
}

