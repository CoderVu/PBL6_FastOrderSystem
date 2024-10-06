// AuthController.java
package com.example.BE_PBL6_FastOrderSystem.controller.Public;

import com.example.BE_PBL6_FastOrderSystem.repository.RoleRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.FormRequest;
import com.example.BE_PBL6_FastOrderSystem.request.RefreshRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.request.LoginRequest;
import com.example.BE_PBL6_FastOrderSystem.response.JwtResponse;
import com.example.BE_PBL6_FastOrderSystem.security.jwt.JwtUtils;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IAuthService;
import com.example.BE_PBL6_FastOrderSystem.service.IFormService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import org.springframework.http.HttpHeaders;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final IFormService formService;


    @PostMapping("/register-user")
    public ResponseEntity<APIRespone> registerUser(@RequestBody User user) {
        return authService.registerUser(user);
    }
    @PostMapping("/register-shipper")
    public ResponseEntity<APIRespone> registerShipper(@RequestBody User user) {
        return authService.registerShipper(user);
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
    @PostMapping("/send-otp")
    public ResponseEntity<APIRespone> resetPassword(@RequestParam String email) {
        return authService.SendOTP(email);
    }
    @PostMapping("/confirm-otp")
    public ResponseEntity<APIRespone> verifyOTP(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        return authService.confirmOTP(email, otp, newPassword);
    }
    @GetMapping("/login-google")
    public ResponseEntity<?> loginGoogle(@AuthenticationPrincipal OAuth2User oauth2User) {
        // Redirect to Google login page
        return ResponseEntity.status(HttpStatus.FOUND)
                             .header(HttpHeaders.LOCATION, "/oauth2/authorization/google")
                             .build();
    }
    @GetMapping("/login-google-callback")
    public ResponseEntity<APIRespone> loginGoogleSuccess(@AuthenticationPrincipal OAuth2User oauth2User) throws Exception {
        if (oauth2User == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User information is missing", null));
        }
        return authService.loginGoogle(oauth2User);
    }
    @GetMapping("/login-facebook")
    public ResponseEntity<?> loginFacebook() {
        // Redirect to Facebook login page
        return ResponseEntity.status(HttpStatus.FOUND)
                             .header(HttpHeaders.LOCATION, "/oauth2/authorization/facebook")
                             .build();
    }
    @GetMapping("/login-facebook-callback")
    public ResponseEntity<APIRespone> loginFacebookSuccess(@AuthenticationPrincipal OAuth2User oauth2User) throws Exception {
        if (oauth2User == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User information is missing", null));
        }
        return authService.loginFacebook(oauth2User);
    }
    @GetMapping("/oauth2/callback")
    public ResponseEntity<APIRespone> loginSuccess(@AuthenticationPrincipal OAuth2User oauth2User) throws Exception {
        return authService.loginSuccess(oauth2User);
    }
    @PostMapping("/shipper-registration")
    public ResponseEntity<APIRespone> addForm(
            @RequestParam("name") String name,
            @RequestParam("citizenID") int citizenID,
            @RequestParam("imageCitizenFront") MultipartFile imageCitizenFront,
            @RequestParam("imageCitizenBack") MultipartFile imageCitizenBack,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("age") Integer age,
            @RequestParam("vehicle") String vehicle,
            @RequestParam("licensePlate") String licensePlate,
            @RequestParam("driverLicense") String driverLicense) {
            FormRequest formRequest = new FormRequest(name, citizenID, imageCitizenFront, imageCitizenBack, email, phone, address, age, vehicle, licensePlate, driverLicense);
            return formService.addForm(formRequest);
    }
}