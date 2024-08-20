package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.*;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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

    private String generateUniqueOrderCode() {
        Random random = new Random();
        String orderCode;
        do {
            orderCode = String.format("%06d", random.nextInt(900000) + 100000); // Generate a random 6-digit number
        } while (orderRepository.existsByOrderCode(orderCode)); // Check if the order code already exists
        return orderCode;
    }

    @Override
    public OrderResponse placeOrder(Long userId, String paymentMethod, Long cartId, String deliveryAddress) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<CartItem> cartItems = cartItemRepository.findByUserIdAndCartId(userId, cartId);
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(cartItems.get(0).getStatus());
        order.setOrderCode(generateUniqueOrderCode());
        order.setCreatedAt(LocalDateTime.now());

        order.setUpdatedAt(LocalDateTime.now());

        Store store = storeRepository.findById(cartItems.get(0).getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        order.setStore(store);

        PaymentMethod paymentMethod1 = paymentMethodRepository.findByName(paymentMethod)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));
        order.setPaymentMethod(paymentMethod1);
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
        order.setTotalAmount(orderDetails.stream().mapToDouble(OrderDetail::getTotalPrice).sum());
        order.setDeliveryAddress(deliveryAddress);
        orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);

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