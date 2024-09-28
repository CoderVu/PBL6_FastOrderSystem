package com.example.BE_PBL6_FastOrderSystem.security.user;

import com.example.BE_PBL6_FastOrderSystem.exception.CustomAuthenticationException;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FoodUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(username);
        if (user == null) {
            user = userRepository.findByEmail(username);
        }
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username or phone number: " + username);
        }
        return FoodUserDetails.buildUserDetails(user);
    }


}