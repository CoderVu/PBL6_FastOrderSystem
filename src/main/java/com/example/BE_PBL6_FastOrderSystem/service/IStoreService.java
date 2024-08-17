package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.request.StoreRequest;
import com.example.BE_PBL6_FastOrderSystem.response.StoreResponse;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;

import java.util.List;

public interface IStoreService {
    Store getStoreById(Long storeId);

    List<StoreResponse> getAllStores();

    StoreResponse addStore(StoreRequest storeRequest);

    StoreResponse updateStore(Long id, StoreRequest storeRequest);

    void deleteStore(Long id);
}
