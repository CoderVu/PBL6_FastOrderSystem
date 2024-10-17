package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.entity.Role;
import com.example.BE_PBL6_FastOrderSystem.entity.User;
import com.example.BE_PBL6_FastOrderSystem.repository.RoleRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.JwtResponse;
import com.example.BE_PBL6_FastOrderSystem.security.jwt.JwtUtils;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.service.IAuthService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;

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
        if (user.getAccountLocked()) {
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
                    userDetails.getEmail(), userDetails.getFullName(), userDetails.getPhoneNumber(), userDetails.getAddress(), userDetails.getLongitude(), userDetails.getLatitude(), userDetails.getAvatar(),
                    userDetails.getCreatedAt(), userDetails.getUpdatedAt(), userDetails.isAccountLocked(), userDetails.getIsActive(), jwt, roles)));
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Phone number is should be 10 digits and start with 0", ""));
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
    public ResponseEntity<APIRespone> registerShipper(User user) {
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
        Optional<Role> optionalRole = roleRepository.findByName("ROLE_SHIPPER");
        if (optionalRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "ROLE_SHIPPER not found", ""));
        }
        Role userRole = optionalRole.get();
        user.setRole(userRole);
        user.setIsApproved(Boolean.valueOf(false));
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
    @Override
    public ResponseEntity<APIRespone> approveShipper(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "User not found", ""));
        }
        User user = optionalUser.get();
        if (user.getRole().getName().equals("ROLE_SHIPPER")) {
            user.setIsApproved(true);
            userRepository.save(user);
            return ResponseEntity.ok(new APIRespone(true, "Success", ""));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "User is not a shipper", ""));
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
    public ResponseEntity<APIRespone> loginGoogle(OAuth2User oauth2User) throws Exception {
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

    @Override
    public ResponseEntity<APIRespone> loginFacebook(OAuth2User oauth2User) throws Exception {
        System.out.println("OAuth2 User attributes: " + oauth2User.getAttributes());
        String name = oauth2User.getAttribute("name");
        String facebookId = oauth2User.getAttribute("id");
        Optional<User> optionalUser = Optional.ofNullable((User) userRepository.findByFacebookId(facebookId));
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = new User();
            user.setFullName(name);
            user.setFacebookId(facebookId);
            user.setAccountLocked(false);
            user.setRole(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("ROLE_USER not found")));
            userRepository.save(user);
        }
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
    @Override
    public ResponseEntity<APIRespone> loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) throws Exception {
        if (oAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "Login failed", ""));
        }
        String email = oAuth2User.getAttribute("email");
        String facebookId = oAuth2User.getAttribute("id");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        // check if it's a facebook login or google login
        if (facebookId == null) {
            return handleGoogleLogin(oAuth2User, email, name, picture);
        } else if (facebookId != null) {
            return handleFacebookLogin(oAuth2User, facebookId, name);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new APIRespone(false, "Invalid login", ""));
    }

    private ResponseEntity<APIRespone> handleGoogleLogin(OAuth2User oauth2User, String email, String name, String picture) throws Exception {
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

        return buildJwtResponse(user);
    }
    private ResponseEntity<APIRespone> handleFacebookLogin(OAuth2User oauth2User, String facebookId, String name) throws Exception {
        Optional<User> optionalUser = Optional.ofNullable((User) userRepository.findByFacebookId(facebookId));
        User user;

        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            user = new User();
            user.setFullName(name);
            user.setFacebookId(facebookId);
            user.setAccountLocked(false);
            user.setRole(roleRepository.findByName("ROLE_USER").orElseThrow(() -> new RuntimeException("ROLE_USER not found")));
            userRepository.save(user);
        }

        return buildJwtResponse(user);
    }
    private ResponseEntity<APIRespone> buildJwtResponse(User user) throws Exception {
        // Convert User to FoodUserDetails
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

}