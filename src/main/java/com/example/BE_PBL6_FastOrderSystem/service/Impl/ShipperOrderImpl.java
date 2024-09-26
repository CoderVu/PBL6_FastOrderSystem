package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.*;
import com.example.BE_PBL6_FastOrderSystem.repository.OrderDetailRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.ShipperOrderRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.ShipperRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.StatusOrderRepository;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.OrderDetailResponse;
import com.example.BE_PBL6_FastOrderSystem.response.ShipperOrderResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import com.example.BE_PBL6_FastOrderSystem.service.IShipperOrderService;
import com.example.BE_PBL6_FastOrderSystem.service.IStatusOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShipperOrderImpl implements IShipperOrderService {
    private final ShipperOrderRepository shipperOrderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ShipperRepository shipperRepository;
    private final StatusOrderRepository statusOrderRepository;
    private final IStatusOrderService statusOrderService;

    private final IOrderService orderService;

    @Override
    public ResponseEntity<APIRespone> getShipperOrderbyId(Long shipperId, Long shipperOrderId) {
        ShipperOrder shipperOrder = shipperOrderRepository.findByIdAndShipperId(shipperOrderId, shipperId);
        if (shipperOrder == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No orders found for the specified shipper", ""));
        }
        List<OrderDetailResponse> orderDetailResponses = shipperOrder.getOrderDetails().stream()
                .map(OrderDetailResponse::new)
                .collect(Collectors.toList());
        ShipperOrderResponse shipperOrderResponse = new ShipperOrderResponse(shipperOrder);
        shipperOrderResponse.setOrderDetails(orderDetailResponses);
        return ResponseEntity.ok(new APIRespone(true, "Success", shipperOrderResponse));
    }

    @Override
    public ResponseEntity<APIRespone> approveShipperOrder(Long shipperId, Long shipperOrderId, Boolean isAccepted) {
        ShipperOrder shipperOrder = shipperOrderRepository.findByIdAndShipperId(shipperOrderId, shipperId);
        if (shipperOrder == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No orders found for the specified shipper", ""));
        }

        if (isAccepted) {
            // shipper chấp nhận đơn hàng
            shipperOrder.setStatus(true);
            shipperOrderRepository.save(shipperOrder);
            // cập nhật trạng thái của shipper
            User shipper = shipperOrder.getShipper();
            shipper.setIsActive(false);
            return ResponseEntity.ok(new APIRespone(true, "Shipper accepted the order", ""));
        } else {
            // shipper từ chối đơn hàng
            shipperOrder.setStatus(false);
            // tìm shipper khác thay thế
            Store store = shipperOrder.getStore();
            Optional<User> newShipperOptional = shipperRepository.findNearestShippers(store.getLatitude(), store.getLongitude(), 1)
                    .stream()
                    .findFirst();

            if (newShipperOptional.isPresent()) {
                User newShipper = newShipperOptional.get();
                newShipper.setIsActive(true);
                shipperRepository.save(newShipper);
                shipperOrder.setShipper(newShipper);
                shipperOrderRepository.save(shipperOrder);
                return ResponseEntity.ok(new APIRespone(true, "Shipper declined. New shipper assigned.", ""));
            } else {
                return ResponseEntity.badRequest().body(new APIRespone(false, "No available shippers found", ""));
            }
        }
    }
    @Override
    public ResponseEntity<APIRespone> updateStatusOrderDetail(Long shipperId, Long shipperOrderId, Long orderDetailId) {
        ShipperOrder shipperOrder = shipperOrderRepository.findByIdAndShipperId(shipperOrderId, shipperId);
        if (shipperOrder == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No orders found for the specified shipper", ""));
        }
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId).orElse(null);
        if (orderDetail == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No OrderDetail found with the specified ID", ""));
        }
        if (!orderDetail.getShipperOrder().getShipper().getId().equals(shipperId)) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "The OrderDetail is not being delivered by the specified shipper", ""));
        }
        StatusOrder statusOrder = statusOrderRepository.findByStatusName("Đơn hàng đang giao");
        if (statusOrder != null) {
            orderDetail.setStatus(statusOrder);
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status not found", ""));
        }
        orderDetailRepository.save(orderDetail);
        return ResponseEntity.ok(new APIRespone(true, "OrderDetail status updated successfully", ""));
    }

}
