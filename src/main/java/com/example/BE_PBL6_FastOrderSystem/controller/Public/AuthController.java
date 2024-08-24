// AuthController.java
package com.example.BE_PBL6_FastOrderSystem.controller.Public;

import com.example.BE_PBL6_FastOrderSystem.request.RefreshRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.request.LoginRequest;
import com.example.BE_PBL6_FastOrderSystem.security.jwt.JwtUtils;
import com.example.BE_PBL6_FastOrderSystem.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;


    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
      return authService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<APIRespone> authenticateUser(@Valid @RequestBody LoginRequest request) {
        return authService.authenticateUser(request.getNumberPhone(), request.getPassword());
    }
    @PostMapping("/logout")
    public ResponseEntity<APIRespone> logoutUser(@RequestHeader("Authorization") String token)
    {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            authService.logout(token);
            return new ResponseEntity<>(new APIRespone(true, "Logout success", ""), HttpStatus.OK);
    }
    return new ResponseEntity<>(new APIRespone(true, "Logout success", ""), HttpStatus.OK);
    }
    @PostMapping("/refresh")
    public ResponseEntity<APIRespone> refreshToken(@RequestBody RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (jwtUtils.validateToken(refreshToken)) {
            String newAccessToken = jwtUtils.generateTokenFromRefreshToken(refreshToken);
            authService.invalidateToken(refreshToken); // Thu hồi token cũ
            return ResponseEntity.ok(new APIRespone(true, "Token refreshed successfully", newAccessToken));
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Invalid token", null));
        }
    }
}