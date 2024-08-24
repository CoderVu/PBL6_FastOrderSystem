package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.Role;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.repository.RoleRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.RefreshRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.JwtResponse;
import com.example.BE_PBL6_FastOrderSystem.security.jwt.JwtUtils;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IAuthService;
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
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public ResponseEntity<APIRespone> authenticateUser(String numberPhone, String password) {
        if (userRepository.findByPhoneNumber(numberPhone) == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "Phone number is required", ""));
        }
        if (userRepository.findByPhoneNumber(numberPhone).getPassword() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "Password is required", ""));
        }
        if (userRepository.findByPhoneNumber(numberPhone).isAccountLocked()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "Account is locked", ""));
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(numberPhone, password));
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "Invalid phone number or password", ""));
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
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Email is required", ""));
        }
        if (user.getAddress() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Address is required", ""));
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
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Email is required", ""));
        }
        if (user.getAddress() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Address is required", ""));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<Role> optionalRole = roleRepository.findByName("ROLE_ADMIN");
        if (optionalRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "ROLE_ADMIN not found", ""));
        }
        Role adminRole = optionalRole.get();
        user.setRole(adminRole);
        userRepository.save(user);
        return ResponseEntity.ok(new APIRespone(true, "Success", ""));
    }

    @Override
    public ResponseEntity<APIRespone> refreshToken(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        if (jwtUtils.validateToken(refreshToken)) {
            String newAccessToken = jwtUtils.generateTokenFromRefreshToken(refreshToken);
            return ResponseEntity.ok(new APIRespone(true, "Token refreshed successfully", newAccessToken));
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Invalid token", null));
        }
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


}