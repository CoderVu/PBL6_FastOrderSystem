package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.exception.UserNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.model.User;
import com.example.BE_PBL6_FastOrderSystem.repository.StoreRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.StoreRequest;
import com.example.BE_PBL6_FastOrderSystem.response.StoreResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImlp implements IStoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    @Override
    public Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId).orElse(null);
    }

    @Override
    public List<StoreResponse> getAllStores() {
       return storeRepository.findAll().stream()
               .map(store -> new StoreResponse(
                       store.getStoreId(),
                       store.getStoreName(),
                       store.getLocation(),
                       store.getLatitude(),
                       store.getLongitude(),
                       store.getPhoneNumber(),
                       store.getOpeningTime(),
                       store.getClosingTime(),
                       store.getCreatedAt(),
                       store.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public StoreResponse addStore(StoreRequest storeRequest) {
        if (storeRepository.existsByStoreName(storeRequest.getStoreName())) {
            throw new AlreadyExistsException("Product already exists");
        }
        Store store = new Store();
        store.setStoreName(storeRequest.getStoreName());
        store.setPhoneNumber(storeRequest.getPhoneNumber());
        store.setLocation(storeRequest.getLocation());
        store.setLongitude(storeRequest.getLongitude());
        store.setLatitude(storeRequest.getLatitude());
        store.setOpeningTime(storeRequest.getOpeningTime());
        store.setClosingTime(storeRequest.getClosingTime());
        User manager = userRepository.findById(storeRequest.getManagerId())
                .orElseThrow(() -> new UserNotFoundException("Manager not found"));
        store.setManager(manager);
        storeRepository.save(store);
        return new StoreResponse(
                store.getStoreId(),
                store.getStoreName(),
                store.getLocation(),
                store.getLatitude(),
                store.getLongitude(),
                store.getPhoneNumber(),
                store.getOpeningTime(),
                store.getClosingTime(),
                store.getCreatedAt(),
                store.getUpdatedAt()
        );


    }

    @Override
    public StoreResponse updateStore(Long id, StoreRequest storeRequest) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new AlreadyExistsException("Store not found"));
        store.setStoreName(storeRequest.getStoreName());
        store.setPhoneNumber(storeRequest.getPhoneNumber());
        store.setLocation(storeRequest.getLocation());
        store.setLongitude(storeRequest.getLongitude());
        store.setLatitude(storeRequest.getLatitude());
        store.setOpeningTime(storeRequest.getOpeningTime());
        store.setClosingTime(storeRequest.getClosingTime());
        User manager = userRepository.findById(storeRequest.getManagerId())
                .orElseThrow(() -> new UserNotFoundException("Manager not found"));
        store.setManager(manager);
        storeRepository.save(store);
        return new StoreResponse(
                store.getStoreId(),
                store.getStoreName(),
                store.getLocation(),
                store.getLatitude(),
                store.getLongitude(),
                store.getPhoneNumber(),
                store.getOpeningTime(),
                store.getClosingTime(),
                store.getCreatedAt(),
                store.getUpdatedAt()
        );
    }

    @Override
    public void deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        storeRepository.delete(store);
    }
}