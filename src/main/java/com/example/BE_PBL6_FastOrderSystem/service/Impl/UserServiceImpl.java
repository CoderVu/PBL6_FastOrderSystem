package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.exception.UserNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.Role;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.repository.RoleRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.UserRequest;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetailsService;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import com.example.BE_PBL6_FastOrderSystem.util.ImageGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final FoodUserDetailsService userDetailsService;
    @Override
    public User registerUser(User user) {
        validateUser(user);
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new AlreadyExistsException(user.getPhoneNumber() + " already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AlreadyExistsException(user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        Optional<Role> optionalRole = roleRepository.findByName("ROLE_USER");
        if (optionalRole.isEmpty()) {
            throw new RuntimeException("ROLE_USER not found");
        }
        Role userRole = optionalRole.get();
        user.setRoles(Collections.singletonList(userRole));
        return userRepository.save(user);
    }
    @Override
    public User registerAdmin(User user) {
        validateUser(user);
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new AlreadyExistsException(user.getPhoneNumber() + " already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AlreadyExistsException(user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        Optional<Role> optionalRole = roleRepository.findByName("ROLE_ADMIN");
        if (optionalRole.isEmpty()) {
            throw new RuntimeException("ROLE_USER not found");
        }
        Role userRole = optionalRole.get();
        user.setRoles(Collections.singletonList(userRole));
        return userRepository.save(user);
    }
    @Override
    public List<User> getUsers(String roleName) {
        return userRepository.findAllByRoles_Name(roleName);
    }
    @Override
    public void lockUserAccount(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setAccountLocked(true);
        userRepository.save(user);
    }
    @Override
    public User getUserProfile(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        validateUserRequest(userRequest);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!existingUser.getPhoneNumber().equals(userRequest.getPhoneNumber()) &&
                userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            throw new AlreadyExistsException(userRequest.getPhoneNumber() + " already exists");
        }
        existingUser.setPhoneNumber(userRequest.getPhoneNumber());
        existingUser.setFullName(userRequest.getFullName());
        existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        try {
            InputStream imageInputStream = userRequest.getAvatar().getInputStream();
            String base64Image = ImageGeneral.fileToBase64(imageInputStream);
            existingUser.setAvatar(base64Image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setAddress(userRequest.getAddress());
        userRepository.save(existingUser);
        return new UserResponse(existingUser);
    }
    @Override
    public void unlockUserAccount(Long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setAccountLocked(false);
        userRepository.save(user);
    }

    private void validateUser(User user) {
        if (user.getFullName() == null || user.getFullName().isEmpty()) {
            throw new AlreadyExistsException("Full name is required");
        }
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new AlreadyExistsException("Password must be at least 8 characters long");
        }
        if (user.getPhoneNumber() == null || !user.getPhoneNumber().matches("\\d{10}") || user.getPhoneNumber().indexOf("0") != 0)
        {
            throw new AlreadyExistsException("Phone number is invalid");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new AlreadyExistsException("Invalid email format");
        }
        if (user.getAddress() == null || user.getAddress().isEmpty()) {
            throw new AlreadyExistsException("Address is required");
        }
    }
    private void validateUserRequest(UserRequest userRequest) {
        if (userRequest.getFullName() == null || userRequest.getFullName().isEmpty()) {
            throw new AlreadyExistsException("Full name is required");
        }
        if (userRequest.getPassword() == null || userRequest.getPassword().length() < 8) {
            throw new AlreadyExistsException("Password must be at least 8 characters long");
        }
        if (userRequest.getPhoneNumber() == null || !userRequest.getPhoneNumber().matches("\\d{10}") || userRequest.getPhoneNumber().indexOf("0") != 0)
        {
            throw new AlreadyExistsException("Phone number is invalid");
        }
        if (userRequest.getEmail() == null || !userRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new AlreadyExistsException("Invalid email format");
        }
        if (userRequest.getAddress() == null || userRequest.getAddress().isEmpty()) {
            throw new AlreadyExistsException("Address is required");
        }
    }
}