package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.UserNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.entity.User;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.UserRequest;
import com.example.BE_PBL6_FastOrderSystem.request.UserRequestV2;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetailsService;
import com.example.BE_PBL6_FastOrderSystem.service.IUserService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<APIRespone> getLocations(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }

        User user = optionalUser.get();
        Map<String, Double> location = Map.of("latitude", user.getLatitude(), "longitude", user.getLongitude());
        APIRespone response = new APIRespone(true, "User location found", location);
        return ResponseEntity.ok(response);
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
        if (userRequest.getAvatar() != null) {
            try {
                InputStream imageInputStream = userRequest.getAvatar().getInputStream();
                String base64Image = ImageGeneral.fileToBase64(imageInputStream);
                existingUser.setAvatar(base64Image);
            } catch (IOException e) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Invalid image", ""));
            }
        }
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setAddress(userRequest.getAddress());
        userRepository.save(existingUser);
        return ResponseEntity.ok(new APIRespone(true, "User updated susccessfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> updateUserV2(Long id, UserRequestV2 userRequest) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIRespone(false, "User not found", ""));
        }

        User existingUser = optionalUser.get();

        // Kiểm tra nếu người dùng yêu cầu thay đổi email
        if (userRequest.getEmail() != null && !userRequest.getEmail().equals(existingUser.getEmail())) {
            // Nếu email mới đã tồn tại trong cơ sở dữ liệu thì trả về lỗi
            if (userRepository.existsByEmail(userRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new APIRespone(false, "Email already exists", ""));
            }

            // Kiểm tra định dạng email
            if (!userRequest.getEmail().matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@([a-zA-Z0-9-]+\\.)+(com|net|org|edu|gov|mil|int)$")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIRespone(false, "Invalid email format", ""));
            }
            existingUser.setEmail(userRequest.getEmail()); // Cập nhật email
        }

        // Kiểm tra tính hợp lệ của địa chỉ nếu có
        if (userRequest.getAddress() != null) {
            if (!userRequest.getAddress().matches("^[\\p{L}0-9\\s,.-]+$")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIRespone(false, "Address must contain only letters, numbers, spaces, commas, periods, and hyphens", ""));
            }
            existingUser.setAddress(userRequest.getAddress()); // Cập nhật địa chỉ
        }

        // Cập nhật các trường khác nếu có
        if (userRequest.getFullName() != null) {
            existingUser.setFullName(userRequest.getFullName());
        }
        if (userRequest.getAvatar() != null) {
            existingUser.setAvatar(userRequest.getAvatar());
        }

        userRepository.save(existingUser);
        return ResponseEntity.ok(new APIRespone(true, "User updated successfully", ""));
    }



    @Override
    public ResponseEntity<APIRespone> updateUserV3(Long id, UserRequestV2 userRequest) {

        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new APIRespone(false, "User not found", ""));
        }
        User existingUser = optionalUser.get();
        existingUser.setFullName(userRequest.getFullName());
        if (userRequest.getAvatar() != null) {
            existingUser.setAvatar(userRequest.getAvatar());
        }
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setAddress(userRequest.getAddress());
        existingUser.setLatitude(userRequest.getLatitude());
        existingUser.setLongitude(userRequest.getLongitude());
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

    @Override
    public ResponseEntity<APIRespone> getByid(Long id){
        User user = userRepository.findByid(id);
        UserResponse userResponse = new UserResponse(user);
        return ResponseEntity.ok(new APIRespone(true, "Success", userResponse));
    }
    @Override
    public   ResponseEntity<APIRespone> searchByName(String name){
        List<User> list = new ArrayList<>();
        for(User user : userRepository.findAll()){
            if(user.getFullName().toLowerCase().contains(name.toLowerCase())){
                list.add(user);
            }
        }
        List<UserResponse> userResponses = list.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", userResponses));

    }
    @Override
    public ResponseEntity<APIRespone> countOrderByMonth(){
        LocalDateTime now = LocalDateTime.now();
        Long result = userRepository.getAllPeopleRegisterByMonth(now.getMonthValue(),now.getYear());
        return  ResponseEntity.ok(new APIRespone(true, "Success", result));
    }

    @Override
    public List<UserResponse> getSearchByPhoneNumber(String phoneNumber){
        List<User> users = userRepository.SearchUsersByPhoneNumber(phoneNumber);

        List<UserResponse> userResponses = users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        return userResponses;
    }

    @Override
    public ResponseEntity<APIRespone> resetPassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        User user = optionalUser.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Old password is incorrect", ""));
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(new APIRespone(true, "Password reset successfully", ""));
    }
}