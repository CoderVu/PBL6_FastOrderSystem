// AuthController.java
package com.example.BE_PBL6_FastOrderSystem.controller.Public;

import com.example.BE_PBL6_FastOrderSystem.repository.RoleRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.RefreshRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.request.LoginRequest;
import com.example.BE_PBL6_FastOrderSystem.response.JwtResponse;
import com.example.BE_PBL6_FastOrderSystem.security.jwt.JwtUtils;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IAuthService;
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


    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
      System.out.println(user);
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
    @PostMapping("/send-otp")
    public ResponseEntity<APIRespone> resetPassword(@RequestParam String email) {
        return authService.SendOTP(email);
    }
    @PostMapping("/confirm-otp")
    public ResponseEntity<APIRespone> verifyOTP(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {
        return authService.confirmOTP(email, otp, newPassword);
    }
    @GetMapping("/login-google")
    public ResponseEntity<?> loginGoogle() {
        // Redirect to Google login page
        return ResponseEntity.status(HttpStatus.FOUND)
                             .header(HttpHeaders.LOCATION, "/oauth2/authorization/google")
                             .build();
    }

    @GetMapping("/login-google-success")
    public ResponseEntity<APIRespone> loginGoogleSuccess(@AuthenticationPrincipal OAuth2User oauth2User) throws Exception {
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");
        String base64Image = ImageGeneral.urlToBase64(picture);
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = new User();
            user.setEmail(email);
            user.setFullName(name);
            user.setAvatar(base64Image);
            user.setRole(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("ROLE_USER not found")));
            userRepository.save(user);
        }
        // Chuyển đổi User thành FoodUserDetails
        FoodUserDetails userDetails = FoodUserDetails.buildUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String jwt = jwtUtils.generateToken(authentication);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return ResponseEntity.ok(new APIRespone(true, "Login successful", new JwtResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getAvatar(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isAccountLocked(),
                jwt,
                roles
        )));
    }

    @GetMapping("/login-google-failure")
    public ResponseEntity<?> loginGoogleFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Login failed"));
    }



}