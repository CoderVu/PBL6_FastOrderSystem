package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.*;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.request.OrderRequest;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    public OrderResponse createOrder(Long userId ,OrderRequest orderRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Order order = new Order();
        order.setUser(user);
        order.setStore(storeRepository.findById(orderRequest.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found")));
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(orderRequest.getTotalAmount());
        order.setStatus(orderRequest.getStatus());
        PaymentMethod paymentMethod = paymentMethodRepository.findByName(orderRequest.getPaymentMethod())
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));
        order.setPaymentMethod(paymentMethod);
        order.setDeliveryAddress(orderRequest.getDeliveryAddress());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        List<OrderDetail> orderDetails = orderRequest.getOrderDetails().stream().map(detailRequest -> {
            Product product = productRepository.findById(detailRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(detailRequest.getQuantity());
            orderDetail.setUnitPrice(detailRequest.getUnitPrice());
            orderDetail.setTotalPrice(detailRequest.getTotalPrice());
            return orderDetail;
        }).collect(Collectors.toList());
        order.setOrderDetails(orderDetails);
        orderRepository.save(order);
        return new OrderResponse(order);
    }
    @Override
    public String updateOrderStatus(Long orderId, Long ownerId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        Store store = order.getStore();
        if (!store.getManager().getId().equals(ownerId)) {
            throw new IllegalStateException("You can only update the status of orders from your own store");
        }
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        return "Đã thanh toán";
    }

    @Override
    public OrderResponse getOrderByIdAndUserId(Long orderId, Long userId) {
        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return new OrderResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrdersByUser(Long userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        if (orders.isEmpty()) {
            throw new ResourceNotFoundException("No orders found for the user");
        }
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }

}