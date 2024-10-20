// AuthController.java
package com.example.BE_PBL6_FastOrderSystem.controller.Public;

import com.example.BE_PBL6_FastOrderSystem.repository.RoleRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.FormRequest;
import com.example.BE_PBL6_FastOrderSystem.request.RefreshRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.entity.User;
import com.example.BE_PBL6_FastOrderSystem.request.LoginRequest;
import com.example.BE_PBL6_FastOrderSystem.response.JwtResponse;
import com.example.BE_PBL6_FastOrderSystem.security.jwt.JwtUtils;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IAuthService;
import com.example.BE_PBL6_FastOrderSystem.service.IFormService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.http.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
@GetMapping("/oauth2/callback/google")
public void handleGoogleCallback(HttpServletResponse response, @AuthenticationPrincipal OAuth2User principal) throws Exception {
    if (principal == null) {
        response.sendRedirect("http://localhost:3000/login?error");
        return;
    }
    String email = principal.getAttribute("email");
    String sub = principal.getAttribute("sub");
    String name = principal.getAttribute("name");
    String picture = principal.getAttribute("picture");
    String base64Image = ImageGeneral.urlToBase64(picture);
    Optional<User> optionalUser = userRepository.findByEmail(email);
    User user;
    if (optionalUser.isPresent()) {
        user = optionalUser.get();
        user.setAvatar(base64Image);
        user.setSub(sub);
        userRepository.save(user);
    } else {
        user = new User();
        user.setEmail(email);
        user.setSub(sub);
        user.setFullName(name);
        user.setAvatar(base64Image);
        user.setAccountLocked(false);
        user.setRole(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("ROLE_USER not found")));
        userRepository.save(user);
    }
    // Convert User to FoodUserDetails
    FoodUserDetails userDetails = FoodUserDetails.buildUserDetails(user);
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    String jwt = jwtUtils.generateToken(authentication);

    response.sendRedirect("http://localhost:3000/oauth2/redirect?token=" + jwt);
}
    @GetMapping("/user-info-google")
    public ResponseEntity<APIRespone> getUserInfo(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer " prefix
        System.out.println("JWT: " + jwt);
        String email = jwtUtils.getUserNameFromToken(jwt);
        System.out.println("Email: " + email);
        Optional<User> optionalUser = userRepository.findByPhoneNumber(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return ResponseEntity.ok(new APIRespone(true, "User info retrieved successfully", new JwtResponse(
                    user.getId(),
                    user.getEmail(),
                    user.getFullName(),
                    user.getPhoneNumber(),
                    user.getAddress(),
                    user.getLongitude(),
                    user.getLatitude(),
                    user.getAvatar(),
                    user.getCreatedAt(),
                    user.getUpdatedAt(),
                    user.getAccountLocked(),
                    user.getIsActive(),
                    jwt,
                    List.of("ROLE_USER")
            )));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "User not found", null));
        }
    }


@GetMapping("/user-info-facebook")
public ResponseEntity<APIRespone> getUserInfoFacebook(@AuthenticationPrincipal OAuth2User principal) throws Exception {
    if (principal == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "User not authenticated", null));
    }
    System.out.println("User Attributes: " + principal.getAttributes());
    String email = principal.getAttribute("email") != null ? principal.getAttribute("email") : "";
    String facebookId = principal.getAttribute("id");
    String name = principal.getAttribute("name");

    Map<String, Object> pictureData = (Map<String, Object>) principal.getAttribute("picture");
    String pictureUrl = null;
    if (pictureData != null && pictureData.get("data") != null) {
        Map<String, Object> data = (Map<String, Object>) pictureData.get("data");
        pictureUrl = (String) data.get("url");
    }

    if (facebookId == null || name == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Invalid Facebook profile information: Missing ID or name", null));
    }

    String base64Image = null;
    try {
        if (pictureUrl != null) {
            base64Image = ImageGeneral.urlToBase64(pictureUrl);
        }
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APIRespone(false, "Failed to convert image: " + e.getMessage(), null));
    }

    Optional<User> optionalUser = userRepository.findByFacebookId(facebookId);
    User user;

    if (optionalUser.isPresent()) {
        user = optionalUser.get();
        user.setAvatar(base64Image);
        user.setFacebookId(facebookId);
        user.setAccountLocked(false);
        userRepository.save(user);
    } else {
        user = new User();
        user.setEmail(email);
        user.setFacebookId(facebookId);
        user.setFullName(name);
        user.setAvatar(base64Image);
        user.setAccountLocked(false);
        user.setRole(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("ROLE_USER not found")));
        userRepository.save(user);
    }

    // Convert User to FoodUserDetails
    FoodUserDetails userDetails = FoodUserDetails.buildUserDetails(user);
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    String jwt = jwtUtils.generateToken(authentication);

    List<String> roles = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

    // Return API response with user details
    return ResponseEntity.ok(new APIRespone(true, "Success", new JwtResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getPhoneNumber(),
            user.getAddress(),
            user.getLongitude(),
            user.getLatitude(),
            user.getAvatar(),
            user.getCreatedAt(),
            user.getUpdatedAt(),
            user.getAccountLocked(),
            user.getIsActive(),
            jwt,
            roles
    )));
}


    @PostMapping("/shipper-registration")
    public ResponseEntity<APIRespone> addForm(
            @RequestParam("name") String name,
            @RequestParam("citizenID") String citizenID,
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