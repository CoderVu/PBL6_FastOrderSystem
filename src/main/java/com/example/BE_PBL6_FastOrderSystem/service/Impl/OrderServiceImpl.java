package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.*;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public String generateUniqueOrderCode() {
        Random random = new Random();
        String orderCode;
        do {
            orderCode = String.format("%06d", random.nextInt(900000) + 100000);
        } while (orderRepository.existsByOrderCode(orderCode));
        return orderCode;
    }

    @Override
    public ResponseEntity<APIRespone> placeOrder(Long UserId, String paymentMethod, List<Long> cartIds, String deliveryAddress,String orderCode) {
        List<CartItem> cartItems = cartIds.stream()
                .flatMap(cartId -> cartItemRepository.findByCartId(cartId).stream())
                .collect(Collectors.toList());
        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts are empty", ""));
        }
        Long storeId = cartItems.get(0).getStoreId();
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (storeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        Store store = storeOptional.get();
        Optional<PaymentMethod> paymentMethodOptional = paymentMethodRepository.findByName(paymentMethod);
        if (paymentMethodOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Payment method not found", ""));
        }
        PaymentMethod paymentMethodEntity = paymentMethodOptional.get();
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(cartItems.get(0).getStatus());
        order.setOrderCode(orderCode); // Để tránh trùng mã order
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStore(store);
        Optional<User> userOptional = userRepository.findById(UserId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        User user = userOptional.get();
        order.setUser(user);
        order.setPaymentMethod(paymentMethodEntity);
        List<OrderDetail> orderDetails = cartItems.stream().map(cartItem -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(cartItem.getUnitPrice());
            orderDetail.setTotalPrice(cartItem.getTotalPrice());
            return orderDetail;
        }).collect(Collectors.toList());
        order.setOrderDetails(orderDetails);
        order.setTotalAmount(orderDetails.stream().mapToDouble(OrderDetail::getTotalPrice).sum()); // Tính tổng tiền
        order.setDeliveryAddress(deliveryAddress);
        orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);
        return ResponseEntity.ok(new APIRespone(true, "Order placed successfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> updateOrderStatus(Long orderId, Long ownerId, String status) {
        if (orderRepository.findById(orderId).isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order not found", ""));
        }
        Order order = orderRepository.findById(orderId).get();
        Store store = order.getStore();
        if (!store.getManager().getId().equals(ownerId)) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "You are not authorized to update this order", ""));
        }
        order.setStatus(status);
        orderRepository.save(order);
        return ResponseEntity.ok(new APIRespone(true, "Order status updated successfully", new OrderResponse(order)));
    }

    @Override
    public ResponseEntity<APIRespone> getOrderByIdAndUserId(Long orderId, Long userId) {
        Optional<Order> orderOptional = orderRepository.findByOrderIdAndUserId(orderId, userId);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order not found", ""));
        }
        Order order = orderOptional.get();
        return ResponseEntity.ok(new APIRespone(true, "Success", new OrderResponse(order)));
    }

    @Override
    public ResponseEntity<APIRespone> getAllOrdersByUser(Long userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }
        List<OrderResponse> orderResponses = orders.stream().map(OrderResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", orderResponses));
    }

    @Override
    public List<CartItem> getCartItemsByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    @Override
    public ResponseEntity<APIRespone> getStatusOrder(Long orderId, Long userId) {
        Optional<Order> orderOptional = orderRepository.findByOrderIdAndUserId(orderId, userId);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order not found", ""));
        }
        Order order = orderOptional.get();
        return ResponseEntity.ok(new APIRespone(true, "Success", order.getStatus()));
    }
}