package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.request.StaffRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

public interface IStaffService {
    ResponseEntity<APIRespone> createStaff(StaffRequest request);

    ResponseEntity<APIRespone> getAllStaff();

    ResponseEntity<APIRespone> getStaffById(Long id);

    ResponseEntity<APIRespone> getStaffByStoreId(Long storeId);

    ResponseEntity<APIRespone> getStaffByStaffCode(String staffCode);

    ResponseEntity<APIRespone> updateStaff(Long id, StaffRequest request);

    ResponseEntity<APIRespone> deleteStaff(Long id);
}
