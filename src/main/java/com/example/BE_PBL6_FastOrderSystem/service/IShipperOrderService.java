package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import org.springframework.http.ResponseEntity;

public interface IShipperOrderService {
    ResponseEntity<APIRespone> getAll();
    ResponseEntity<APIRespone> getShipperOrderbyId(Long shipperId, Long shipperOrderId);

    ResponseEntity<APIRespone> getAllShipperOrderByStatus(Long shipperId, String status);

    ResponseEntity<APIRespone> getAllShipperOrder(Long shipperId);

    ResponseEntity<APIRespone> updateShipperLocation(Long shipperId, Double newLatitude, Double newLongitude);

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
//    ResponseEntity<APIRespone> getOrdersSortedByDistance(Long shipperId);

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
    ResponseEntity<APIRespone> getOrdersSortedByDistance(Long shipperId, int page, int size);

    ResponseEntity<APIRespone> approveShipperOrder(Long shipperId, Long shipperOrderId, Boolean isAccepted);

    ResponseEntity<APIRespone> updateStatusOrderDetail(Long shipperId, Long shipperOrderId, Long orderDetailId);

    ResponseEntity<APIRespone> finishDelivery(Long shipperId, Long shipperOrderId, Long orderDetailId);


    ResponseEntity<APIRespone> updateBusyStatus(Long shipperId);
}
