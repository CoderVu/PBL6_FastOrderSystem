package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.service.IAuthService;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {
    private final IUserService userService;
    private final IAuthService authService;
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody User user) {
        try{
            authService.registerAdmin(user);
            return ResponseEntity.ok("Registration successful!");
        }catch (AlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    @GetMapping("/all_roles")
    public ResponseEntity<APIRespone> getUsers(@RequestParam String role) {
        return userService.getUsers(role);
    }
    @PostMapping("/lock-account/{userId}")
    public ResponseEntity<APIRespone> lockUserAccount(@PathVariable Long userId) {
      return userService.lockUserAccount(userId);
    }
    @PostMapping("/unlock-account/{userId}")
    public ResponseEntity<APIRespone> unlockUserAccount(@PathVariable Long userId) {
        return userService.unlockUserAccount(userId);
    }

}
