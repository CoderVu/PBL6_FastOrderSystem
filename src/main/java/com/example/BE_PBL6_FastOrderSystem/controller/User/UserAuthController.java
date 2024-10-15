package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.request.UserRequest;
import com.example.BE_PBL6_FastOrderSystem.request.UserRequestV2;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetailsService;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PutMapping(value = "/profile/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<APIRespone> updateUser(@ModelAttribute UserRequest userRequest) {
        String fullName = userRequest.getFullName();
        MultipartFile avatar = userRequest.getAvatar();
        String email = userRequest.getEmail();
        String address = userRequest.getAddress();
        Long userId = FoodUserDetails.getCurrentUserId();
        UserRequest userRequest1 = new UserRequest(fullName, avatar, email, address);
        return userService.updateUser(userId, userRequest1);
    }

    @PutMapping("/profiles/updates")
    public ResponseEntity<APIRespone> updateUsers(@RequestBody UserRequestV2 userRequest) {
        Long userId = FoodUserDetails.getCurrentUserId();
        return userService.updateUserV2(userId, userRequest);
    }

    @PutMapping("/profile/add-phone")
    public ResponseEntity<APIRespone> addPhone(@RequestParam("phone") String phone) {
        Long userId = FoodUserDetails.getCurrentUserId();
        return userService.addPhone(userId, phone);
    }

    @GetMapping("/location/{userId}")
    public ResponseEntity<APIRespone> getUserLocation(@PathVariable Long userId) {
        return userService.getLocations(userId);
    }
}

