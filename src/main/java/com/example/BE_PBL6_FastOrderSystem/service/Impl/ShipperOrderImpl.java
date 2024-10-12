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
import java.util.Comparator;
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
//    @Scheduled(fixedRate = 300000) // 5 phút
//    public void autoAssignNewShipper() {
//        List<ShipperOrder> unconfirmedShipperOrders = shipperOrderRepository.findAllByStatusIn(Arrays.asList("Chưa nhận", "Đã từ chối"));
//        System.out.println("Unconfirmed shipper orders: " + unconfirmedShipperOrders.size());
//        for (ShipperOrder shipperOrder : unconfirmedShipperOrders) {
//            if (shipperOrder.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
//                // 5 phút chưa nhận đơn hàng thì tự động gán shipper khác
//                User currentShipper = shipperOrder.getShipper();
//                if (currentShipper != null) {
//                    currentShipper.setIsBusy(true);
//                    currentShipper.setIsActive(true);
//                    shipperRepository.save(currentShipper);
//                    System.out.println("Current shipper deactivated: " + currentShipper.getId());
//                }
//
//                if (shipperOrder.getLastAssignedAt() == null || shipperOrder.getLastAssignedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
//                    Store store = shipperOrder.getStore();
//                    Optional<User> newShipperOptional = shipperRepository.findNearestShippers(store.getLatitude(), store.getLongitude(), 1)
//                            .stream()
//                            .findFirst();
//
//                    if (newShipperOptional.isPresent()) {
//                        User newShipper = newShipperOptional.get();
//                        System.out.println("New shipper assigned: " + newShipper.getId());
//                        newShipper.setIsActive(false);
//                        newShipper.setIsBusy(false);
//                        shipperRepository.save(newShipper);
//
//                        shipperOrder.setShipper(newShipper);
//                        shipperOrder.setLastAssignedAt(LocalDateTime.now());
//                        shipperOrderRepository.save(shipperOrder);
//                    } else {
//                        System.out.println("No available shippers found");
//                    }
//                }
//            }
//        }
//    } // 10 giây
@Override
public ResponseEntity<APIRespone> getOrdersSortedByDistance(Long shipperId, int page, int size) {
    User shipper = shipperRepository.findById(shipperId).orElse(null);
    if (shipper == null) {
        return ResponseEntity.badRequest().body(new APIRespone(false, "No shipper found with the specified ID", ""));
    }

    List<OrderDetail> orderDetails = orderDetailRepository.findAllByStatus("Đơn hàng mới");
    List<OrderDetailResponse> sortedOrderDetails = orderDetails.stream()
            .sorted(Comparator.comparingDouble(orderDetail -> calculateDistance(
                    shipper.getLatitude(), shipper.getLongitude(),
                    orderDetail.getStore().getLatitude(),
                    orderDetail.getStore().getLongitude())))
            .skip(page * size)
            .limit(size)
            .map(OrderDetailResponse::new)
            .collect(Collectors.toList());

    return ResponseEntity.ok(new APIRespone(true, "Success", sortedOrderDetails));
}

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
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
