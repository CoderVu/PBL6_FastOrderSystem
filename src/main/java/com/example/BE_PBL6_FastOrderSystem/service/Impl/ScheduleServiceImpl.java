package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.entity.Schedule;
import com.example.BE_PBL6_FastOrderSystem.entity.Staff;
import com.example.BE_PBL6_FastOrderSystem.repository.ScheduleRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.StaffRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.StoreRepository;
import com.example.BE_PBL6_FastOrderSystem.request.ScheduleRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.ScheduleResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IScheduleService;
import com.example.BE_PBL6_FastOrderSystem.service.IStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements IScheduleService {
    private final IStaffService staffService;
    private final StaffRepository staffRepository;
    private final StoreRepository storeRepository;
    private final ScheduleRepository scheduleRepository;
    @Override
    public ResponseEntity<APIRespone> createSchedule(ScheduleRequest request) {
        Schedule schedule = new Schedule();
        schedule.setShift(request.getShift());
        schedule.setStartShift(request.getStartShift());
        schedule.setEndShift(request.getEndShift());
        schedule.setDate(request.getDate());
        Staff staff = staffRepository.findById(request.getStaffId()).orElse(null);
        if (staff == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Staff not found", null));
        }
        schedule.setStaff(staff);
        scheduleRepository.save(schedule);
        return ResponseEntity.ok(new APIRespone(true, "Create schedule successfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> getAllSchedule() {
        if (scheduleRepository.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new APIRespone(false, "No schedule found", null));
        }
        List<ScheduleResponse> scheduleResponses = scheduleRepository.findAll().stream()
                .map(schedule -> new ScheduleResponse(schedule.getId(), schedule.getShift(), schedule.getStartShift(), schedule.getEndShift(), schedule.getDate(), schedule.getStaff().getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Get all schedule successfully", scheduleResponses));
    }
    @Override
    public ResponseEntity<APIRespone> getScheduleByStoreId(Long storeId) {
        if (staffRepository.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new APIRespone(false, "No schedule found", null));
        }
        List<ScheduleResponse> scheduleResponses = scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getStaff().getStore().getStoreId().equals(storeId))
                .map(schedule -> new ScheduleResponse(schedule.getId(), schedule.getShift(), schedule.getStartShift(), schedule.getEndShift(), schedule.getDate(), schedule.getStaff().getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Get schedule by store id successfully", scheduleResponses));
    }
    @Override
    public ResponseEntity<APIRespone> getScheduleById(Long id) {
        if (scheduleRepository.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Schedule not found", null));
        }
        List<ScheduleResponse> scheduleResponses = scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getId().equals(id))
                .map(schedule -> new ScheduleResponse(schedule.getId(), schedule.getShift(), schedule.getStartShift(), schedule.getEndShift(), schedule.getDate(), schedule.getStaff().getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Get schedule by id successfully", scheduleResponses));
    }
    @Override
    public ResponseEntity<APIRespone> getScheduleByEmployeeName(String employeeName) {
        if (staffRepository.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new APIRespone(false, "No schedule found", null));
        }
        List<ScheduleResponse> scheduleResponses = scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getStaff().getEmployeeName().equals(employeeName))
                .map(schedule -> new ScheduleResponse(schedule.getId(), schedule.getShift(), schedule.getStartShift(), schedule.getEndShift(), schedule.getDate(), schedule.getStaff().getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Get schedule by employee name successfully", scheduleResponses));

    }
    @Override
    public ResponseEntity<APIRespone> getScheduleByStaffId(Long staffId) {
        if (staffRepository.findAll().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new APIRespone(false, "No schedule found", null));
        }
        List<ScheduleResponse> scheduleResponses = scheduleRepository.findAll().stream()
                .filter(schedule -> schedule.getStaff().getId().equals(staffId))
                .map(schedule -> new ScheduleResponse(schedule.getId(), schedule.getShift(), schedule.getStartShift(), schedule.getEndShift(), schedule.getDate(), schedule.getStaff().getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Get schedule by staff id successfully", scheduleResponses));
    }

    @Override
    public ResponseEntity<APIRespone> updateSchedule(Long id, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Schedule not found", null));
        }
        schedule.setShift(request.getShift());
        schedule.setStartShift(request.getStartShift());
        schedule.setEndShift(request.getEndShift());
        schedule.setDate(request.getDate());
        Staff staff = staffRepository.findById(request.getStaffId()).orElse(null);
        if (staff == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Staff not found", null));
        }
        schedule.setStaff(staff);
        scheduleRepository.save(schedule);
        return ResponseEntity.ok(new APIRespone(true, "Update schedule successfully", ""));
    }
    @Override
    public ResponseEntity<APIRespone> deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id).orElse(null);
        if (schedule == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIRespone(false, "Schedule not found", null));
        }
        scheduleRepository.delete(schedule);
        return ResponseEntity.ok(new APIRespone(true, "Delete schedule successfully", null));
    }


}
