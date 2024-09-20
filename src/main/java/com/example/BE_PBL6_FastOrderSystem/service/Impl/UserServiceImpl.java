package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.exception.UserNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.repository.RoleRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.UserRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetailsService;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FoodUserDetailsService userDetailsService;

    @Override
    public ResponseEntity<APIRespone> getUsers(String roleName) {
        List<User> users = userRepository.findAllByRole_Name(roleName);
        if (users.isEmpty()) {
            return ResponseEntity.ok(new APIRespone(false, "No user found", ""));
        }
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", userResponses));
    }

    @Override
    public ResponseEntity<APIRespone> lockUserAccount(Long userId) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        User user = optionalUser.get();
        user.setAccountLocked(true);
        userRepository.save(user);
        return ResponseEntity.ok(new APIRespone(true, "Success", ""));
    }

    @Override
    public ResponseEntity<APIRespone> getUserProfile(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        User user = optionalUser.get();
        UserResponse userResponse = new UserResponse(user);
        return ResponseEntity.ok(new APIRespone(true, "Success", userResponse));
    }
    @Override
    public ResponseEntity<APIRespone> updateUser(Long id, UserRequest userRequest) {
        ResponseEntity<APIRespone> validationResponse = validateUserRequest(userRequest);
        if (validationResponse != null) {
            return validationResponse;
        }
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIRespone(false, "User not found", ""));
        }
        User existingUser = optionalUser.get();
        existingUser.setFullName(userRequest.getFullName());
        try {
            InputStream imageInputStream = userRequest.getAvatar().getInputStream();
            String base64Image = ImageGeneral.fileToBase64(imageInputStream);
            existingUser.setAvatar(base64Image);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIRespone(false, "Failed to process avatar image", ""));
        }
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setAddress(userRequest.getAddress());
        userRepository.save(existingUser);
        return ResponseEntity.ok(new APIRespone(true, "User updated susccessfully", ""));
    }

    private ResponseEntity<APIRespone> validateUserRequest(UserRequest userRequest) {
        if (userRequest.getFullName() == null || userRequest.getFullName().isEmpty() || !userRequest.getFullName().matches("^[\\p{L}\\s]+$")) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Full name is required and must contain only letters and spaces", ""));
        }
        if (userRequest.getEmail() == null || !userRequest.getEmail().matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@([a-zA-Z0-9-]+\\.)+(com|net|org|edu|gov|mil|int)$")) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Invalid email format", ""));
        }
        if (userRequest.getAddress() == null || userRequest.getAddress().isEmpty() || !userRequest.getAddress().matches("^[\\p{L}0-9\\s,.-]+$")) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Address is required and must contain only letters, numbers, spaces, commas, periods, and hyphens", ""));
        }
        return null;
    }


    @Override
    public ResponseEntity<APIRespone> unlockUserAccount(Long userId) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        User user = optionalUser.get();
        user.setAccountLocked(false);
        userRepository.save(user);
        return ResponseEntity.ok(new APIRespone(true, "Success", ""));
    }

    @Override
    public UserDetails loadUserByNumberPhone(String numberPhone) {
        return userDetailsService.loadUserByUsername(numberPhone);
    }

    @Override
    public ResponseEntity<APIRespone> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.ok(new APIRespone(false, "No user found", ""));
        }
        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", userResponses));
    }

    @Override
    public ResponseEntity<APIRespone> addPhone(Long userId, String phone) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        if (!phone.matches("\\d{10}") || !phone.startsWith("0")) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Invalid phone number format", ""));
        }
        if (userRepository.existsByPhoneNumber(phone)) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Phone number already exists", ""));
        }
        User user = optionalUser.get();
        user.setPhoneNumber(phone);
        userRepository.save(user);
        return ResponseEntity.ok(new APIRespone(true, "Success", ""));
    }
}