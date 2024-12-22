package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.entity.Store;
import com.example.BE_PBL6_FastOrderSystem.entity.User;
import com.example.BE_PBL6_FastOrderSystem.repository.StoreRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.StoreRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.APIResponseChat;
import com.example.BE_PBL6_FastOrderSystem.response.StoreResponse;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IStoreService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import com.example.BE_PBL6_FastOrderSystem.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImlp implements IStoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    @Override
    public ResponseEntity<APIRespone> getStoreByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        User user = userOptional.get();
        List<Store> stores = storeRepository.findAllByManagerId(user.getId());
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No store found for this user", ""));
        }
        List<StoreResponse> storeResponses = stores.stream()
                .map(store -> new StoreResponse(
                        store.getStoreId(),
                        store.getStoreName(),
                        store.getImage(),
                        store.getLocation(),
                        store.getLongitude(),
                        store.getLatitude(),

                        store.getPhoneNumber(),
                        store.getOpeningTime(),
                        store.getClosingTime(),

                        store.getCreatedAt(),
                        store.getUpdatedAt(),
                        store.getManager().getId()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", storeResponses));
    }

    @Override
    public ResponseEntity<APIRespone> getStoreById(Long storeId) {
        if (storeRepository.findById(storeId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        Store store = storeRepository.findById(storeId).get();
        return ResponseEntity.ok(new APIRespone(true, "Success", new StoreResponse(
                store.getStoreId(),
                store.getStoreName(),
                store.getImage(),
                store.getLocation(),

                store.getLongitude(),
                store.getLatitude(),
                store.getPhoneNumber(),
                store.getOpeningTime(),
                store.getClosingTime(),

                store.getCreatedAt(),
                store.getUpdatedAt(),
                store.getManager().getId()
        )));
    }

    @Override
    public ResponseEntity<APIRespone> getAllStores() {
        if (storeRepository.findAll().isEmpty()) {
            return ResponseEntity.ok(new APIRespone(false, "No store found", ""));
        }
        List<StoreResponse> storeResponses = storeRepository.findAll().stream()
                .map(store -> new StoreResponse(
                        store.getStoreId(),
                        store.getStoreName(),
                        store.getImage(),
                        store.getLocation(),
                        store.getLongitude(),
                        store.getLatitude(),

                        store.getPhoneNumber(),
                        store.getOpeningTime(),
                        store.getClosingTime(),

                        store.getCreatedAt(),
                        store.getUpdatedAt(),
                        store.getManager().getId()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", storeResponses));
    }
    @Override
    public ResponseEntity<APIRespone> addStore(StoreRequest storeRequest) {
        Store store = new Store();

        // Kiểm tra tên cửa hàng
        if (storeRequest.getStoreName() == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store name is required", ""));
        }
        if (storeRepository.existsByStoreName(storeRequest.getStoreName())) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store already exists", ""));
        }
        store.setStoreName(storeRequest.getStoreName());

        // Kiểm tra số điện thoại
        if (storeRequest.getPhoneNumber() == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Phone number is required", ""));
        }
        store.setPhoneNumber(storeRequest.getPhoneNumber());

        // Kiểm tra latitude
        if (storeRequest.getLatitude() == null || storeRequest.getLatitude() < -90 || storeRequest.getLatitude() > 90 || storeRequest.getLatitude() == 0 ) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Latitude is required", ""));
        }
        store.setLatitude(storeRequest.getLatitude());

        // Kiểm tra longitude
        if (storeRequest.getLongitude() == null || storeRequest.getLongitude() < -180 || storeRequest.getLongitude() > 180 || storeRequest.getLongitude() == 0) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Longitude is required", ""));
        }
        store.setLongitude(storeRequest.getLongitude());

        // Kiểm tra địa chỉ cửa hàng
        if (storeRequest.getLocation() == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Location is required", ""));
        }
        store.setLocation(storeRequest.getLocation());

        // Kiểm tra thời gian mở cửa
        if (storeRequest.getOpeningTime() == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Opening time is required", ""));
        }
        store.setOpeningTime(storeRequest.getOpeningTime());

        // Kiểm tra thời gian đóng cửa
        if (storeRequest.getClosingTime() == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Closing time is required", ""));
        }
        store.setClosingTime(storeRequest.getClosingTime());

        // Kiểm tra quản lý cửa hàng
        if (userRepository.findById(storeRequest.getManagerId()).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Manager not found", ""));
        }
        User manager = userRepository.findById(storeRequest.getManagerId()).get();
        store.setManager(manager);

        // Kiểm tra hình ảnh
        if (storeRequest.getImage() == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Image is required", ""));
        }
        try {
            String normalizedStoreName = StringUtils.normalizeString(storeRequest.getStoreName());
            String timestamp = LocalDateTime.now().format(StringUtils.formatter);
            String imageName = normalizedStoreName + "_" + timestamp + ".png";
            Path imagePath = Paths.get("uploads/images/" + imageName);
            Files.createDirectories(imagePath.getParent());
            Files.copy(storeRequest.getImage().getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            store.setImage(imageName);
        }
        catch (IOException e) {
            return new ResponseEntity<>(new APIRespone(false, "Error when uploading image", ""), HttpStatus.BAD_REQUEST);
        }

        // Lưu store sau khi tất cả các trường đã được kiểm tra
        store = storeRepository.save(store);
        return ResponseEntity.ok(new APIRespone(true, "Add store successfully", new StoreResponse(
                store.getStoreId(),
                store.getStoreName(),
                store.getImage(),
                store.getLocation(),
                store.getLongitude(),
                store.getLatitude(),
                store.getPhoneNumber(),
                store.getOpeningTime(),
                store.getClosingTime(),
                store.getCreatedAt(),
                store.getUpdatedAt(),
                store.getManager().getId()
        )));
    }


    @Override
    public ResponseEntity<APIRespone> updateStore(Long id, StoreRequest storeRequest) {
        Optional<Store> storeOptional = storeRepository.findById(id);
        if (storeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        Store store = storeOptional.get();

        if (storeRequest.getStoreName() != null) {
            if (storeRepository.existsByStoreName(storeRequest.getStoreName())) {
                Optional<Store> existingStore = storeRepository.findByStoreName(storeRequest.getStoreName());
                if (existingStore.isPresent() && !existingStore.get().getStoreId().equals(id)) {
                    return ResponseEntity.badRequest().body(new APIRespone(false, "Store already exists", ""));
                }
            }
            store.setStoreName(storeRequest.getStoreName());
        }

        if (storeRequest.getImage() != null) {
            try {
                String normalizedStoreName = StringUtils.normalizeString(storeRequest.getStoreName());
                String timestamp = LocalDateTime.now().format(StringUtils.formatter);
                String imageName = normalizedStoreName + "_" + timestamp + ".png";
                Path imagePath = Paths.get("uploads/images/" + imageName);
                Files.createDirectories(imagePath.getParent());
                Files.copy(storeRequest.getImage().getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                store.setImage(imageName);
            } catch (IOException e) {
                return new ResponseEntity<>(new APIRespone(false, "Error when uploading image", ""), HttpStatus.BAD_REQUEST);
            }
        }

        if (storeRequest.getPhoneNumber() == null || !storeRequest.getPhoneNumber().matches("\\d{10}") || storeRequest.getPhoneNumber().indexOf("0") != 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Phone number is should be 10 digits and start with 0", ""));
        }

        store.setPhoneNumber(storeRequest.getPhoneNumber());
        // Kiểm tra địa chỉ cửa hàng
        if (storeRequest.getLocation() == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Location is required", ""));
        }
        store.setLocation(storeRequest.getLocation());
        // Kiểm tra latitude
        if (storeRequest.getLatitude() == null || storeRequest.getLatitude() < -90 || storeRequest.getLatitude() > 90 || storeRequest.getLatitude() == 0 ) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Latitude is required", ""));
        }
        store.setLatitude(storeRequest.getLatitude());

        // Kiểm tra longitude
        if (storeRequest.getLongitude() == null || storeRequest.getLongitude() < -180 || storeRequest.getLongitude() > 180 || storeRequest.getLongitude() == 0) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Longitude is required", ""));
        }
        store.setLongitude(storeRequest.getLongitude());

        if (storeRequest.getOpeningTime() != null) {
            store.setOpeningTime(storeRequest.getOpeningTime());
        }
        if (storeRequest.getClosingTime() != null) {
            store.setClosingTime(storeRequest.getClosingTime());
        }
        if (storeRequest.getManagerId() != null) {
            Optional<User> managerOptional = userRepository.findById(storeRequest.getManagerId());
            if (managerOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Manager not found", ""));
            }
            User manager = managerOptional.get();
            store.setManager(manager);
        }
        storeRepository.save(store);
        return ResponseEntity.ok(new APIRespone(true, "Update store successfully", ""));
    }
    @Override
    public ResponseEntity<APIRespone> deleteStore(Long id) {
        Optional<Store> store = storeRepository.findById(id);
        if (store.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        storeRepository.deleteById(id);
        return ResponseEntity.ok(new APIRespone(true, "Delete store successfully", ""));
    }

    @Override
    public APIResponseChat<UserResponse> getOwnerForStore(Long id) {
        Optional<Store> store = storeRepository.findById(id);
        if (store.isEmpty()) {
            return new APIResponseChat<>(null,1,"Store not found");
        }
        Store store1 = store.get();
        User owner = store1.getManager();
        UserResponse ow = new UserResponse(owner);
        return new APIResponseChat<>(ow,0,"Owner for store " + store1.getStoreName() + " is " + owner.getFullName());
    }

}