package com.example.BE_PBL6_FastOrderSystem.security.user;

import com.example.BE_PBL6_FastOrderSystem.entity.User;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FoodUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByPhoneNumber(username);
        if (user.isEmpty()) {
            user = userRepository.findByEmail(username);
        }
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
        return FoodUserDetails.buildUserDetails(user.orElse(null));
    }


}