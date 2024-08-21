package com.example.BE_PBL6_FastOrderSystem.controller.User;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.request.UserRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;
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
    @GetMapping("/profile/{userId}")
    public ResponseEntity<APIRespone> getUserProfile(@PathVariable("userId") String userId) {
      return userService.getUserProfile(userId);
    }
    @PostMapping("/profile/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId,
                                        @RequestParam("phoneNumber") String phoneNumber,
                                        @RequestParam("password") String password,
                                        @RequestParam("fullName") String fullName,
                                        @RequestParam("avatar") MultipartFile avatar,
                                        @RequestParam("email") String email,
                                        @RequestParam("address") String address) {
       try {
             UserRequest userResquest = new UserRequest(phoneNumber, password, fullName, avatar, email, address);
              userService.updateUser(userId, userResquest);
                return ResponseEntity.ok().body("User updated successfully");
            } catch (AlreadyExistsException e) {
           return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
       }
    }
}
