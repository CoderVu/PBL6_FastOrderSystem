package com.example.BE_PBL6_FastOrderSystem.controller.Admin;

import com.example.BE_PBL6_FastOrderSystem.exception.UserAlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.exception.UserNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.security.jwt.JwtUtils;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/auth")
public class AdminAuthController {
    private final IUserService userService;
    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody User user) {
        try{
            userService.registerAdmin(user);
            return ResponseEntity.ok("Registration successful!");
        }catch (UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    @GetMapping("/all_roles")
    public ResponseEntity<List<User>> getUsers(@RequestParam String role) {
        try {
            List<User> users = userService.getUsers(role);
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/lock-account/{userId}")
    public ResponseEntity<?> lockUserAccount(@PathVariable Long userId) {
        try {
            userService.lockUserAccount(userId);
            return ResponseEntity.ok("User account locked successfully!");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @PostMapping("/unlock-account/{userId}")
    public ResponseEntity<?> unlockUserAccount(@PathVariable Long userId) {
        try {
            userService.unlockUserAccount(userId);
            return ResponseEntity.ok("User account unlocked successfully!");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
