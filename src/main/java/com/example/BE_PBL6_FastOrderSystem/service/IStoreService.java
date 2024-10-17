package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.request.StoreRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

public interface IStoreService {
    ResponseEntity<APIRespone> getStoreByUserId(Long userId);

    ResponseEntity<APIRespone> getStoreById(Long storeId);

    ResponseEntity<APIRespone> getAllStores();

    ResponseEntity<APIRespone> addStore(StoreRequest storeRequest);

    ResponseEntity <APIRespone>  updateStore(Long id, StoreRequest storeRequest);

    ResponseEntity<APIRespone> deleteStore(Long id);

}
