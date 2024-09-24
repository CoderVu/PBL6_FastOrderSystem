package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.ShipperOrder;
import com.example.BE_PBL6_FastOrderSystem.repository.ShipperOrderRepository;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.ShipperOrderResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IShipperOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShipperOrderImpl implements IShipperOrderService {
    final ShipperOrderRepository shipperOrderRepository;
    final OrderServiceImpl orderService;
    @Override
    public ResponseEntity<APIRespone> getShipperOrderbyOderDetailId(Long shipperId) {
        List<ShipperOrder> shipperOrders = shipperOrderRepository.findAllByShipperId(shipperId);
        if (shipperOrders.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No orders found for the specified shipper", ""));
        }
        List<ShipperOrderResponse> shipperOrderResponses = shipperOrders.stream()
                .map(ShipperOrderResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new APIRespone(true, "Success", shipperOrderResponses));
    }
   // lay ra 1 don hang cu the
    @Override
    public ResponseEntity<APIRespone> getShipperOrderbyId(Long shipperOrderId) {
        ShipperOrder shipperOrder = shipperOrderRepository.findById(shipperOrderId).orElse(null);
        if (shipperOrder == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No orders found for the specified shipper", ""));
        }
        ShipperOrderResponse shipperOrderResponse = new ShipperOrderResponse(shipperOrder);
        return ResponseEntity.ok(new APIRespone(true, "Success", shipperOrderResponse));
    }

}
