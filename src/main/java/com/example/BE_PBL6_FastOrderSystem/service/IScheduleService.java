package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.request.ScheduleRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

public interface IScheduleService {
    ResponseEntity<APIRespone> createSchedule(ScheduleRequest request);

    ResponseEntity<APIRespone> getAllSchedule();

    ResponseEntity<APIRespone> getScheduleByStoreId(Long storeId);

    ResponseEntity<APIRespone> getScheduleById(Long id);

    ResponseEntity<APIRespone> getScheduleByEmployeeName(String employeeName);

    ResponseEntity<APIRespone> getScheduleByStaffId(Long staffId);

    ResponseEntity<APIRespone> updateSchedule(Long id, ScheduleRequest request);

    ResponseEntity<APIRespone> deleteSchedule(Long id);
}
