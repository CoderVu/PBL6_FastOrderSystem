package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.entity.StatusOrder;
import com.example.BE_PBL6_FastOrderSystem.repository.OrderRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.StatusOrderRepository;
import com.example.BE_PBL6_FastOrderSystem.request.StatusOrderRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.StatusOrderResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IStatusOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusOrderServiceImpl implements IStatusOrderService {
    private final StatusOrderRepository statusOrderRepository;
    private final OrderRepository orderRepository;
    @Override
    public ResponseEntity<APIRespone> getAllStatus() {
        if (statusOrderRepository.findAll().isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No size found", ""));
        }
        List<StatusOrderResponse> statusResponses = statusOrderRepository.findAll().stream()
                .map(statusOrder -> new StatusOrderResponse(statusOrder.getStatusId(), statusOrder.getStatusName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", statusResponses));
    }
    @Override
    public ResponseEntity<APIRespone> getStatusById(Long statusId) {
        if (statusOrderRepository.findById(statusId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status not found", ""));
        }
        return ResponseEntity.ok(new APIRespone(true, "Success", statusOrderRepository.findById(statusId).get()));
    }
    @Override
    public ResponseEntity<APIRespone> createStatus(StatusOrderRequest statusOrderRequest) {
        if (statusOrderRepository.findByStatusName(statusOrderRequest.getStatusName()) != null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status already exists", ""));
        }
        StatusOrder statusOrder = new StatusOrder();
        statusOrder.setStatusName(statusOrderRequest.getStatusName());
        statusOrderRepository.save(statusOrder);
        return ResponseEntity.ok(new APIRespone(true, "Success", statusOrder));
    }
    @Override
    public ResponseEntity<APIRespone> updateStatus(Long statusId, StatusOrderRequest statusOrderRequest) {
        if (statusOrderRepository.findById(statusId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status not found", ""));
        }
        StatusOrder statusOrder = statusOrderRepository.findById(statusId).get();
        statusOrder.setStatusName(statusOrderRequest.getStatusName());
        statusOrderRepository.save(statusOrder);
        return ResponseEntity.ok(new APIRespone(true, "Success", statusOrder));
    }
    @Override
    public ResponseEntity<APIRespone> deleteStatus(Long statusId) {
        if (statusOrderRepository.findById(statusId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status not found", ""));
        }
        StatusOrder statusOrder = statusOrderRepository.findById(statusId).get();
        if (orderRepository.findByStatusOrder(statusOrder)) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status is being used", ""));
        }
        statusOrderRepository.delete(statusOrder);
        return ResponseEntity.ok(new APIRespone(true, "Success", ""));
    }
}
