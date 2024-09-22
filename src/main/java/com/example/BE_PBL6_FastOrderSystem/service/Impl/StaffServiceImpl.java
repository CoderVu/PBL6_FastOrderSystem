package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.Staff;
import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.repository.StaffRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.StoreRepository;
import com.example.BE_PBL6_FastOrderSystem.request.StaffRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.StaffResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements IStaffService {
private final StaffRepository staffRepository;
private final StoreRepository storeRepository;
    @Override
    public ResponseEntity<APIRespone> createStaff(StaffRequest request) {
        Staff staff = new Staff();
        staff.setEmployeeName(request.getEmployeeName());
        if (staffRepository.findByStaff_code(request.getStaff_code()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Staff code already exists", null));
        }
        staff.setStaff_code(request.getStaff_code());
        staff.setDepartment(request.getDepartment());
        if (request.getStoreId() != null) {
            Optional<Store> optionalStore = storeRepository.findById(request.getStoreId());
            if (optionalStore.isPresent()) {
                staff.setStore(optionalStore.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Store not found", null));
            }
        }
        staffRepository.save(staff);
        return ResponseEntity.ok(new APIRespone(true, "Create staff successfully", ""));
    }
    @Override
    public ResponseEntity<APIRespone> getAllStaff() {
        if (staffRepository.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new APIRespone(false, "No staff found", null));
        }
        List<StaffResponse> staffRespons = staffRepository.findAll().stream()
                .map(staff -> new StaffResponse(staff.getId(), staff.getEmployeeName(), staff.getStaff_code(), staff.getDepartment(), staff.getStore().getStoreId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Get all staff successfully", staffRespons));
    }
    @Override
    public ResponseEntity<APIRespone> getStaffById(Long id) {
       if (staffRepository.findById(id).isEmpty()){
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Staff not found", null));
         }
       List<StaffResponse> staffRespons = staffRepository.findAll().stream()
               .filter(staff -> staff.getId().equals(id))
               .map(staff -> new StaffResponse(staff.getId(), staff.getEmployeeName(), staff.getStaff_code(), staff.getDepartment(), staff.getStore().getStoreId()))
               .collect(Collectors.toList());
         return ResponseEntity.ok(new APIRespone(true, "Get staff by id successfully", staffRespons));
    }
    @Override
    public ResponseEntity<APIRespone> getStaffByStoreId(Long storeId) {
        if (staffRepository.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new APIRespone(false, "No staff found", null));
        }
        List<StaffResponse> staffRespons = staffRepository.findAll().stream()
                .filter(staff -> staff.getStore().getStoreId().equals(storeId))
                .map(staff -> new StaffResponse(staff.getId(), staff.getEmployeeName(), staff.getStaff_code(), staff.getDepartment(), staff.getStore().getStoreId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Get staff by store id successfully", staffRespons));
    }
    @Override
    public ResponseEntity<APIRespone> getStaffByStaffCode(String staffCode) {
        Optional<Object> optionalStaff = staffRepository.findByStaff_code(staffCode);
        if (optionalStaff.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Staff not found", null));
        }
        List<StaffResponse> staffRespons = staffRepository.findAll().stream()
                .filter(staff -> staff.getStaff_code().equals(staffCode))
                .map(staff -> new StaffResponse(staff.getId(), staff.getEmployeeName(), staff.getStaff_code(), staff.getDepartment(), staff.getStore().getStoreId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Get staff by staff code successfully", staffRespons));
    }
    @Override
    public ResponseEntity<APIRespone> updateStaff(Long id, StaffRequest request) {
        Staff staff = staffRepository.findById(id).orElse(null);
        if (staff == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Staff not found", null));
        }
        staff.setEmployeeName(request.getEmployeeName());
        if (staffRepository.findByStaff_code(request.getStaff_code()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIRespone(false, "Staff code already exists", null));
        }
        staff.setStaff_code(request.getStaff_code());
        staff.setDepartment(request.getDepartment());
        if (request.getStoreId() != null) {
            Optional<Store> optionalStore = storeRepository.findById(request.getStoreId());
            if (optionalStore.isPresent()) {
                staff.setStore(optionalStore.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Store not found", null));
            }
        }
        staffRepository.save(staff);
        return ResponseEntity.ok(new APIRespone(true, "Update staff successfully", staff));
    }
        @Override
        public ResponseEntity<APIRespone> deleteStaff(Long id) {
             Optional<Staff> optionalStaff = staffRepository.findById(id);
            if (optionalStaff.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Staff not found", null));
            }
            Staff staff = optionalStaff.get();
            staffRepository.delete(staff);
            return ResponseEntity.ok(new APIRespone(true, "Delete staff successfully", null));
        }


}
