package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.repository.StoreRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.StoreRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.StoreResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IStoreService;
import com.example.BE_PBL6_FastOrderSystem.utils.ImageGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImlp implements IStoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

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
                store.getLatitude(),
                store.getLongitude(),
                store.getPhoneNumber(),
                store.getOpeningTime(),
                store.getClosingTime(),
                store.getManager().getFullName(),
                store.getCreatedAt(),
                store.getUpdatedAt()
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
                        store.getLatitude(),
                        store.getLongitude(),
                        store.getPhoneNumber(),
                        store.getOpeningTime(),
                        store.getClosingTime(),
                        store.getManager().getFullName(),
                        store.getCreatedAt(),
                        store.getUpdatedAt()
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
        if (storeRequest.getImage() != null) {
            try {
                InputStream imageInputStream = storeRequest.getImage().getInputStream();
                String base64Image = ImageGeneral.fileToBase64(imageInputStream);
                store.setImage(base64Image);
            } catch (IOException e) {
                return new ResponseEntity<>(new APIRespone(false, "Error when uploading image", ""), HttpStatus.BAD_REQUEST);
            }
        }

        // Lưu store sau khi tất cả các trường đã được kiểm tra
        store = storeRepository.save(store);
        return ResponseEntity.ok(new APIRespone(true, "Add store successfully", new StoreResponse(
                store.getStoreId(),
                store.getStoreName(),
                store.getImage(),
                store.getLocation(),
                store.getLatitude(),
                store.getLongitude(),
                store.getPhoneNumber(),
                store.getOpeningTime(),
                store.getClosingTime(),
                store.getManager().getFullName(),
                store.getCreatedAt(),
                store.getUpdatedAt()
        )));
    }

//    @Override
//    public ResponseEntity<APIRespone> addStore(StoreRequest storeRequest) {
//        Store store = new Store();
//        if (storeRequest.getImage() != null) {
//            try {
//                InputStream imageInputStream = storeRequest.getImage().getInputStream();
//                String base64Image = ImageGeneral.fileToBase64(imageInputStream);
//                store.setImage(base64Image);
//            } catch (IOException e) {
//                return new ResponseEntity<>(new APIRespone(false, "Error when upload image", ""), HttpStatus.BAD_REQUEST);
//            }
//        }
//
//
//
//        if (storeRepository.existsByStoreName(storeRequest.getStoreName())) {
//            return ResponseEntity.badRequest().body(new APIRespone(false, "Store already exists", ""));
//        }
//        if (storeRequest.getStoreName() == null) {
//            return ResponseEntity.badRequest().body(new APIRespone(false, "Store name is required", ""));
//        }
//        store.setStoreName(storeRequest.getStoreName());
//
//
//
//
//        if (storeRequest.getPhoneNumber() == null) {
//            return ResponseEntity.badRequest().body(new APIRespone(false, "Phone number is required", ""));
//        }
//        store.setPhoneNumber(storeRequest.getPhoneNumber());
//
//
//
//
//
//        if (storeRequest.getLatitude() == null) {
//            return ResponseEntity.badRequest().body(new APIRespone(false, "Latitude is required", ""));
//        }
//        store.setLatitude(storeRequest.getLatitude());
//
//
//
//
//        if (storeRequest.getClosingTime() == null) {
//            return ResponseEntity.badRequest().body(new APIRespone(false, "Closing time is required", ""));
//        }
//        store.setClosingTime(storeRequest.getClosingTime());
//
//
//
//
//        if (storeRequest.getLongitude() == null) {
//            return ResponseEntity.badRequest().body(new APIRespone(false, "Longitude is required", ""));
//        }
//        store.setLongitude(storeRequest.getLongitude());
//
//
//
//
//        if (storeRequest.getLocation() == null) {
//            return ResponseEntity.badRequest().body(new APIRespone(false, "Location is required", ""));
//        }
//        store.setLocation(storeRequest.getLocation());
//
//
//
//        if (storeRequest.getOpeningTime() == null) {
//            return ResponseEntity.badRequest().body(new APIRespone(false, "Opening time is required", ""));
//        }
//        store.setOpeningTime(storeRequest.getOpeningTime());
//
//
//
//        User manager = userRepository.findById(storeRequest.getManagerId()).get();
//        if (userRepository.findById(storeRequest.getManagerId()).isEmpty()) {
//            return ResponseEntity.badRequest().body(new APIRespone(false, "Manager not found", ""));
//        }
//        store.setManager(manager);
//        store = storeRepository.save(store);
//        return ResponseEntity.ok(new APIRespone(true, "Add store successfully", new StoreResponse(
//                store.getStoreId(),
//                store.getStoreName(),
//                store.getImage(),
//                store.getLocation(),
//                store.getLatitude(),
//                store.getLongitude(),
//                store.getPhoneNumber(),
//                store.getOpeningTime(),
//                store.getClosingTime(),
//                store.getManager().getFullName(),
//                store.getCreatedAt(),
//                store.getUpdatedAt()
//        )));
//    }

    @Override
    public ResponseEntity<APIRespone> updateStore(Long id, StoreRequest storeRequest) {
        if (storeRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        Store store = storeRepository.findById(id).get();
        if (storeRepository.existsByStoreName(storeRequest.getStoreName())) {
            Optional<Store> existingStore = storeRepository.findByStoreName(storeRequest.getStoreName());
            if (existingStore.isPresent() && !existingStore.get().getStoreId().equals(id)) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Store already exists", ""));
            }
        }
        store.setStoreName(storeRequest.getStoreName());
        try {
            InputStream imageInputStream = storeRequest.getImage().getInputStream();
            String base64Image = ImageGeneral.fileToBase64(imageInputStream);
            store.setImage(base64Image);
        } catch (IOException e) {
            return new ResponseEntity<>(new APIRespone(false, "Error when upload image", ""), HttpStatus.BAD_REQUEST);
        }
        store.setPhoneNumber(storeRequest.getPhoneNumber());
        store.setLocation(storeRequest.getLocation());
        store.setLongitude(storeRequest.getLongitude());
        store.setLatitude(storeRequest.getLatitude());
        store.setOpeningTime(storeRequest.getOpeningTime());
        store.setClosingTime(storeRequest.getClosingTime());
        if (userRepository.findById(storeRequest.getManagerId()).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Manager not found", ""));
        }
        User manager = userRepository.findById(storeRequest.getManagerId()).get();
        store.setManager(manager);
        store = storeRepository.save(store);
        return ResponseEntity.ok(new APIRespone(true, "Update store successfully", new StoreResponse(
                store.getStoreId(),
                store.getStoreName(),
                store.getImage(),
                store.getLocation(),
                store.getLatitude(),
                store.getLongitude(),
                store.getPhoneNumber(),
                store.getOpeningTime(),
                store.getClosingTime(),
                store.getManager().getFullName(),
                store.getCreatedAt(),
                store.getUpdatedAt()
        )));
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

}