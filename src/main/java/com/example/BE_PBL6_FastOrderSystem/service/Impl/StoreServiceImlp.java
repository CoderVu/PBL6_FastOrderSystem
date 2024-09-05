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
        if (storeRepository.existsByStoreName(storeRequest.getStoreName())) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store already exists", ""));
        }
        Store store = new Store();
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