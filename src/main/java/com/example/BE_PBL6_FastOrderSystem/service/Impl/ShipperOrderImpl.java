package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.*;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.OrderDetailResponse;
import com.example.BE_PBL6_FastOrderSystem.response.ShipperOrderResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import com.example.BE_PBL6_FastOrderSystem.service.IShipperOrderService;
import com.example.BE_PBL6_FastOrderSystem.service.IStatusOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShipperOrderImpl implements IShipperOrderService {
    private final ShipperOrderRepository shipperOrderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ShipperRepository shipperRepository;
    private final StatusOrderRepository statusOrderRepository;
    private final IStatusOrderService statusOrderService;
    private final IOrderService orderService;

    @Override
    public ResponseEntity<APIRespone> getAll() {
        List<ShipperOrder> shipperOrders = shipperOrderRepository.findAll();
        List<ShipperOrderResponse> shipperOrderResponses = shipperOrders.stream()
                .map(ShipperOrderResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", shipperOrderResponses));
    }
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
    public ResponseEntity<APIRespone> getAllShipperOrder(Long shipperId) {
        List<ShipperOrder> shipperOrders = shipperOrderRepository.findAllByShipperId(shipperId);
        List<ShipperOrderResponse> shipperOrderResponses = shipperOrders.stream()
                .map(ShipperOrderResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", shipperOrderResponses));
    }
    @Override
    public ResponseEntity<APIRespone> updateShipperLocation(Long shipperId, Double newLatitude, Double newLongitude) {
        User shipper = shipperRepository.findById(shipperId).orElse(null);
        if (shipper == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No shipper found with the specified ID", ""));
        }
        shipper.setLatitude(newLatitude);
        shipper.setLongitude(newLongitude);
        shipperRepository.save(shipper);
        return ResponseEntity.ok(new APIRespone(true, "Shipper location updated successfully", ""));
    }
    @Override
    public ResponseEntity<APIRespone> approveShipperOrder(Long shipperId, Long shipperOrderId, Boolean isAccepted) {
        ShipperOrder shipperOrder = shipperOrderRepository.findByIdAndShipperId(shipperOrderId, shipperId);
        if (shipperOrder == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No orders found for the specified shipper", ""));
        }

        if (isAccepted) {
            // shipper chấp nhận đơn hàng
            shipperOrder.setStatus("Đang giao hàng");
            shipperOrder.setReceivedAt(LocalDateTime.now());
            shipperOrderRepository.save(shipperOrder);
            // cập nhật trạng thái của shipper
            User shipper = shipperOrder.getShipper();
            shipper.setIsActive(true);
            return ResponseEntity.ok(new APIRespone(true, "Shipper accepted the order", ""));
        } else {
            // shipper từ chối đơn hàng
            shipperOrder.setStatus("Đã từ chối");
            shipperOrderRepository.save(shipperOrder);
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
    @Override
    public ResponseEntity<APIRespone> finishDelivery(Long shipperId, Long shipperOrderId, Long orderDetailId)  {
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
        StatusOrder statusOrder = statusOrderRepository.findByStatusName("Đơn hàng đã hoàn thành");
        if (statusOrder != null) {
            orderDetail.setStatus(statusOrder);
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status not found", ""));
        }

        orderDetailRepository.save(orderDetail);
        // kiểm tra xem tất cả các orderDetail cua shipperOrder khác đã hoàn thành chưa neu tat ca hoan thanh thi cap nhat trang thai cua Order la da hoan thanh
        List<OrderDetail> orderDetails = shipperOrder.getOrderDetails();
        boolean allFinished = orderDetails.stream().allMatch(o -> o.getStatus().getStatusName().equals("Đơn hàng đã hoàn thành"));
        if (allFinished) {
            Order order = orderDetails.get(0).getOrder();
            StatusOrder orderStatus = statusOrderRepository.findByStatusName("Đơn hàng đã hoàn thành");
            order.setStatus(orderStatus);
            orderRepository.save(order);
        }
        shipperOrder.setStatus("Đã giao hàng");
        shipperOrder.setReceivedAt(LocalDateTime.now());
        shipperOrderRepository.save(shipperOrder);
        return ResponseEntity.ok(new APIRespone(true, "Order delivered successfully", ""));
    }
}
