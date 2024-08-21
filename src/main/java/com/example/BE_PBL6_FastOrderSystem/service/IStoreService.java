package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.request.StoreRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.StoreResponse;
import com.example.BE_PBL6_FastOrderSystem.response.UserResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IStoreService {
    ResponseEntity<APIRespone> getStoreById(Long storeId);

    ResponseEntity<APIRespone> getAllStores();

    ResponseEntity<APIRespone> addStore(StoreRequest storeRequest);

    ResponseEntity <APIRespone>  updateStore(Long id, StoreRequest storeRequest);

    ResponseEntity<APIRespone> deleteStore(Long id);
}
