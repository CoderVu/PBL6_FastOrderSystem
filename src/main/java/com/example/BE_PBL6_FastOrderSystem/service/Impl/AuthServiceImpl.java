package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.Role;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.repository.RoleRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.JwtResponse;
import com.example.BE_PBL6_FastOrderSystem.security.jwt.JwtUtils;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IAuthService;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.CLIENT_ID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailServiceImpl emailService;
    private final OTPServiceImpl otpService;

    @Override
    public ResponseEntity<APIRespone> authenticateUser(String username, String password) {
        User user = userRepository.findByPhoneNumber(username);
        if (user == null) {
            user = userRepository.findByEmail(username);
        }
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "Username is required", ""));
        }
        if (user.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "Password is required", ""));
        }
        if (user.isAccountLocked()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "Account is locked", ""));
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtTokenForUser(authentication);
            FoodUserDetails userDetails = (FoodUserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            return ResponseEntity.ok(new APIRespone(true, "Success", new JwtResponse((userDetails.getId()),
                    userDetails.getEmail(), userDetails.getFullName(), userDetails.getPhoneNumber(), userDetails.getAddress(),
                    userDetails.getCreatedAt(), userDetails.getUpdatedAt(), userDetails.isAccountLocked(), jwt, roles)));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "Invalid username or password", ""));
        }
    }

    @Override
    public ResponseEntity<APIRespone> registerUser(User user) {
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIRespone(false, user.getPhoneNumber() + " already exists", ""));
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIRespone(false, user.getEmail() + " already exists", ""));
        }
        if (user.getPhoneNumber() == null || !user.getPhoneNumber().matches("\\d{10}") || user.getPhoneNumber().indexOf("0") != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Phone number is required", ""));
        }
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Password must be at least 8 characters long", ""));
        }
        if (user.getFullName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Full name is required", ""));
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@([a-zA-Z0-9-]+\\.)+(com|net|org|edu|gov|mil|int)$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Email is required", ""));
        }
        if (user.getAddress() == null || !user.getAddress().matches("^[\\p{L}0-9\\s,.-]+$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Address is required and must contain only letters, numbers, spaces, commas, periods, and hyphens", ""));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<Role> optionalRole = roleRepository.findByName("ROLE_USER");
        if (optionalRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "ROLE_USER not found", ""));
        }
        Role userRole = optionalRole.get();
        user.setRole(userRole);
        userRepository.save(user);
        return ResponseEntity.ok(new APIRespone(true, "Success", ""));
    }

    @Override
    public ResponseEntity<APIRespone> registerAdmin(User user) {
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIRespone(false, user.getPhoneNumber() + " already exists", ""));
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new APIRespone(false, user.getEmail() + " already exists", ""));
        }
        if (user.getPhoneNumber() == null || !user.getPhoneNumber().matches("\\d{10}") || user.getPhoneNumber().indexOf("0") != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Phone number is required", ""));
        }
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Password must be at least 8 characters long", ""));
        }
        if (user.getFullName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Full name is required", ""));
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@([a-zA-Z0-9-]+\\.)+(com|net|org|edu|gov|mil|int)$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Email is required", ""));
        }
        if (user.getAddress() == null || !user.getAddress().matches("^[\\p{L}0-9\\s,.-]+$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Address is required and must contain only letters, numbers, spaces, commas, periods, and hyphens", ""));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<Role> optionalRole = roleRepository.findByName("ROLE_OWNER");
        if (optionalRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "ROLE_ADMIN not found", ""));
        }
        Role adminRole = optionalRole.get();
        user.setRole(adminRole);
        userRepository.save(user);
        return ResponseEntity.ok(new APIRespone(true, "Success", ""));
    }

    private final Set<String> invalidTokens = new HashSet<>();

    @Override
    public void logout(String token) {
        invalidTokens.add(token);
    }

    @Override
    public boolean isTokenInvalid(String token) {
        return invalidTokens.contains(token);
    }

    @Override
    public void invalidateToken(String refreshToken) {
        invalidTokens.add(refreshToken);
    }

    @Override
    public ResponseEntity<APIRespone> SendOTP(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Email not found", ""));
        }
        user.setId(user.getId());
        String otp = otpService.generateOTP(email, user.getId());
        emailService.sendEmail(email, "Password reset request", "OTP: " + otp);
        return ResponseEntity.ok(new APIRespone(true, "Success", ""));
    }

    @Override
    public ResponseEntity<APIRespone> confirmOTP(String email, String otp, String newPassword) {
        if (otpService.verifyOTP(email, otp)) {
            User user = userRepository.findByEmail(email);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return ResponseEntity.ok(new APIRespone(true, "Password reset successfully", ""));
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Invalid OTP", ""));
        }
    }
    @Override
    public ResponseEntity<APIRespone> currentUser(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
      if (oAuth2AuthenticationToken != null) {
          return ResponseEntity.ok(new APIRespone(true, "Success", oAuth2AuthenticationToken.getPrincipal().getAttributes()));
      } else {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Unauthorized", ""));
      }
    }

    @Override
    public ResponseEntity<APIRespone> authenticateUserWithGoogle(String googleToken) {
        try {
            boolean isValid = validateGoogleToken(googleToken);
            if (isValid) {
                String jwtToken = jwtUtils.generateTokenFromGoogleToken(googleToken);
                return ResponseEntity.ok(new APIRespone(true, "Login successful", jwtToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new APIRespone(false, "Invalid Google token", null));
            }
        } catch (Exception e) {
            // Log the exception and return a 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIRespone(false, "Internal server error", null));
        }
    }

    private boolean validateGoogleToken(String googleToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();
            GoogleIdToken idToken = verifier.verify(googleToken);
            return idToken != null;
        } catch (Exception e) {
            return false;
        }
    }

}