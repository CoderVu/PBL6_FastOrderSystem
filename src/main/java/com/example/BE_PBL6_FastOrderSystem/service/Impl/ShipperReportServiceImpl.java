package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.entity.ShipperOrder;
import com.example.BE_PBL6_FastOrderSystem.repository.ShipperOrderRepository;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.ShipperReportResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IShipperReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipperReportServiceImpl implements IShipperReportService {
    private final ShipperOrderRepository shipperOrderRepository;

    @Override
    public ResponseEntity<APIRespone> getAllShipperReport(Long shipperId) {
        String status = "Đã giao hàng";
        List<ShipperOrder> shipperOrders = shipperOrderRepository.findAllByShipperIdAndStatus(shipperId, status);
        if (shipperOrders.isEmpty()) {
            return ResponseEntity.ok(new APIRespone(false, "No shipper report found", null));
        }

        List<ShipperReportResponse> shipperReportResponses = shipperOrders.stream()
                .map(shipperOrder -> {
                    Set<Long> processedStores = new HashSet<>();
                    double totalShippingFee = shipperOrder.getOrderDetails().stream()
                            .filter(orderDetail -> {
                                Long storeId = orderDetail.getStore().getStoreId();
                                if (processedStores.contains(storeId)) {
                                    return false;
                                } else {
                                    processedStores.add(storeId);
                                    return true;
                                }
                            })
                            .mapToDouble(orderDetail -> orderDetail.getShippingFee() != null ? orderDetail.getShippingFee().getFee() : 0.0)
                            .sum();
                    return new ShipperReportResponse(
                            shipperOrder.getId(),
                            shipperOrder.getShipper().getId(),
                            shipperOrder.getShipper().getFullName(),
                            shipperOrder.getStatus() != null ? shipperOrder.getStatus().getStatusName() : "Unknown",
                            shipperOrder.getReceivedAt(),
                            shipperOrder.getDeliveredAt(),
                            totalShippingFee,
                            shipperOrder.getStore().getStoreName()
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new APIRespone(true, "Get all shipper report successfully", shipperReportResponses));
    }
}