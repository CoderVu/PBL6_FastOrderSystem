package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.UserAlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.model.Role;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.repository.RoleRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public User registerUser(User user) {
        validateUser(user);
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new UserAlreadyExistsException(user.getPhoneNumber() + " already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
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

    private void validateUser(User user) {
        if (user.getFullName() == null || user.getFullName().isEmpty()) {
            throw new UserAlreadyExistsException("Full name is required");
        }
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            throw new UserAlreadyExistsException("Password must be at least 8 characters long");
        }
        if (user.getPhoneNumber() == null || !user.getPhoneNumber().matches("\\d{10}") || user.getPhoneNumber().indexOf("0") != 0)
        {
            throw new UserAlreadyExistsException("Phone number is invalid");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new UserAlreadyExistsException("Invalid email format");
        }
        if (user.getAddress() == null || user.getAddress().isEmpty()) {
            throw new UserAlreadyExistsException("Address is required");
        }
    }
}