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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    public ResponseEntity<APIRespone> getAllShipperOrderByStatus(Long shipperId, String status) {
        List<ShipperOrder> shipperOrders = shipperOrderRepository.findAllByShipperIdAndStatus(shipperId, status);
        System.out.println(shipperOrders);
        List<ShipperOrderResponse> shipperOrderResponses = shipperOrders.stream()
                .map(ShipperOrderResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", shipperOrderResponses));
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
    @Scheduled(fixedRate = 30000) // 30 giây
    public void autoAssignShipper() {
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByStatus("Đơn hàng mới");
        Map<Store, Map<Long, List<OrderDetail>>> groupedOrderDetails = orderDetails.stream()
                .collect(Collectors.groupingBy(OrderDetail::getStore,
                        Collectors.groupingBy(orderDetail -> orderDetail.getOrder().getOrderId())));

        for (Map.Entry<Store, Map<Long, List<OrderDetail>>> storeGroup : groupedOrderDetails.entrySet()) {
            Store store = storeGroup.getKey();
            Map<Long, List<OrderDetail>> orderIdGroups = storeGroup.getValue();

            for (Map.Entry<Long, List<OrderDetail>> orderGroup : orderIdGroups.entrySet()) {
                List<OrderDetail> storeOrderDetails = orderGroup.getValue();
                Optional<User> newShipperOptional = shipperRepository.findNearestShippers(store.getLatitude(), store.getLongitude(), 1)
                        .stream()
                        .findFirst();
                if (newShipperOptional.isPresent()) {
                    User newShipper = newShipperOptional.get();
                    newShipper.setIsActive(false);
                    shipperRepository.save(newShipper);
                    for (OrderDetail orderDetail : storeOrderDetails) {
                        ShipperOrder shipperOrder = new ShipperOrder();
                        shipperOrder.setShipper(newShipper);
                        shipperOrder.setStore(store);
                        shipperOrder.setCreatedAt(LocalDateTime.now());
                        shipperOrder.setStatus("Chưa nhận");
                        shipperOrderRepository.save(shipperOrder);
                        orderDetail.setShipperOrder(shipperOrder);
                        orderDetailRepository.save(orderDetail);
                        // cập nhật trạng thái của orderDetail
                        StatusOrder statusOrder = statusOrderRepository.findByStatusName("Đơn hàng đã chọn được người giao");
                        orderDetail.setStatus(statusOrder);
                        orderDetailRepository.save(orderDetail);
                    }
                }
            }
        }
    }
    @Scheduled(fixedRate = 100000) // 100 giây
    public void autoAssignShipperOrder() {
        List<ShipperOrder> ListShipperOrders = shipperOrderRepository.findAllByStatusIn(List.of("Chưa nhận", "Đã từ chối"));
        for (ShipperOrder shipperOrder : ListShipperOrders ) {
            if (shipperOrder.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
                // 10 phút chưa nhận đơn hàng thì tự động gán shipper khác
                User currentShipper = shipperOrder.getShipper();
                if (currentShipper != null) {
                    currentShipper.setIsActive(true);
                    currentShipper.setIsBusy(true);
                    shipperRepository.save(currentShipper);  // cập nhật trạng thái của shipper cũ
                }
                Store store = shipperOrder.getStore();
                Optional<User> newShipperOptional = shipperRepository.findNearestShippers(store.getLatitude(), store.getLongitude(), 1)
                        .stream()
                        .findFirst();
                if (newShipperOptional.isPresent()) {
                    User newShipper = newShipperOptional.get();
                    newShipper.setIsActive(false);
                    shipperRepository.save(newShipper);
                    shipperOrder.setShipper(newShipper);
                    shipperOrder.setCreatedAt(LocalDateTime.now());
                    shipperOrder.setStatus("Chưa nhận");
                    shipperOrderRepository.save(shipperOrder);
                }
            }
        }
    }
    @Override
    public ResponseEntity<APIRespone> approveShipperOrder(Long shipperId, Long shipperOrderId, Boolean isAccepted) {
        ShipperOrder shipperOrder = shipperOrderRepository.findByIdAndShipperId(shipperOrderId, shipperId);
        if (shipperOrder == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No orders found for the specified shipper", ""));
        }
        if (isAccepted) {
            // shipper chấp nhận đơn hàng
            shipperOrder.setStatus("Đã nhận");
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
                newShipper.setIsActive(false);
                shipperRepository.save(newShipper);
                shipperOrder.setShipper(newShipper);
                shipperOrderRepository.save(shipperOrder);
                return ResponseEntity.ok(new APIRespone(true, "Shipper đã từ chối, hệ thống đã tìm thấy shipper khác", ""));
            } else {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Không tìm thấy shipper khác", ""));
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
