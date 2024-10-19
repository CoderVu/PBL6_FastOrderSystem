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

    @GetMapping("/user-info-google")
    public ResponseEntity<APIRespone> getUserInfo(@AuthenticationPrincipal OAuth2User principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "User not authenticated", null));
        }
        String email = principal.getAttribute("email");
        String sub = principal.getAttribute("sub");
        String name = principal.getAttribute("name");
        String picture = principal.getAttribute("picture");
        String base64Image = ImageGeneral.urlToBase64(picture);
        Optional<User> optionalUser = Optional.ofNullable(userRepository.findByEmail(email));
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
        // Chuyển đổi User thành FoodUserDetails
        FoodUserDetails userDetails = FoodUserDetails.buildUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String jwt = jwtUtils.generateToken(authentication);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
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
    @GetMapping("/user-info-facebook")
    public ResponseEntity<APIRespone> getUserInfoFacebook(@AuthenticationPrincipal OAuth2User principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "User not authenticated", null));
        }
        String email = principal.getAttribute("email");
        String facebookId = principal.getAttribute("id");
        String name = principal.getAttribute("name");
        String picture = principal.getAttribute("picture");
        String base64Image = ImageGeneral.urlToBase64(picture);
        Optional<User> optionalUser = Optional.ofNullable((User) userRepository.findByFacebookId(facebookId));
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
        // Chuyển đổi User thành FoodUserDetails
        FoodUserDetails userDetails = FoodUserDetails.buildUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        String jwt = jwtUtils.generateToken(authentication);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
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