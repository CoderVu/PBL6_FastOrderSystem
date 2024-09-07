package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.*;
import com.example.BE_PBL6_FastOrderSystem.model.ProductStore;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service // đánh dấu đây là một service để spring boot có thể quản lý và inject vào các bean khác trong ứng dụng của bạn
// bean là một đối tượng mà spring framework quản lý và tạo ra nó, nó được quản lý trong container của spring và được sử dụng bởi các class khác trong ứng dụng của bạn thông qua dependency injection
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ComboRepository comboRepository;
    private final ProductStoreRepository productStoreRepository;
    public String generateUniqueOrderCode() {
        Random random = new Random();
        String orderCode;
        do {
            orderCode = String.format("%06d", random.nextInt(900000) + 100000);
        } while (orderRepository.existsByOrderCode(orderCode));
        return orderCode;
    }

    public ResponseEntity<APIRespone> processProductOrder(Long userId, String paymentMethod, List<Long> cartIds, String deliveryAddress, String orderCode) {
        List<Cart> cartItems = cartIds.stream()
                .flatMap(cartId -> cartItemRepository.findByCartId(cartId).stream())
                .filter(cartItem -> cartItem.getUser().getId().equals(userId))
                .collect(Collectors.toList()); // get all cart items by cartId and userId

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts are empty", ""));
        }

        if (cartItems.stream().anyMatch(cartItem -> !cartItem.getUser().getId().equals(userId))) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts does not belong to the specified you! ", ""));
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

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        User user = userOptional.get();

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(cartItems.get(0).getStatus());
        order.setOrderCode(orderCode);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStore(store);
        order.setUser(user);
        order.setPaymentMethod(paymentMethodEntity);
        order.setDeliveryAddress(deliveryAddress);

        List<OrderDetail> orderDetails = cartItems.stream().map(cartItem -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartItem.getProduct());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(cartItem.getUnitPrice());
            orderDetail.setTotalPrice(cartItem.getTotalPrice());
            orderDetail.setSize(cartItem.getSize());
            return orderDetail;
        }).collect(Collectors.toList());
        order.setOrderDetails(orderDetails);
        order.setTotalAmount(orderDetails.stream().mapToDouble(OrderDetail::getTotalPrice).sum());
        orderRepository.save(order);
        System.out.println("Đã lưu order");
        cartItemRepository.deleteAll(cartItems);
        System.out.println("Các sản phẩm đã được xóa khỏi giỏ hàng");
        return ResponseEntity.ok(new APIRespone(true, "Order placed successfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> updateQuantityProductOrderByProduct(Long productId, Long storeId ,int quantity) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found", ""));
        }
        Product product = productOptional.get();
        for (ProductStore productStore : product.getProductStores()) {
            if (productStore.getStore().getStoreId().equals(storeId)) {
                productStore.setStockQuantity(productStore.getStockQuantity() - quantity);
                productStoreRepository.save(productStore);
                return ResponseEntity.ok(new APIRespone(true, "Product quantity updated successfully", ""));
            }
        }
        return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found in store", ""));
    }

    @Override
    public ResponseEntity<APIRespone> updateQuantityProductOrderByCombo(Long comboId, Long storeId, int quantity) {
        Optional<Combo> comboOptional = comboRepository.findById(comboId);
        if (comboOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
        }
        Combo combo = comboOptional.get();
        for (Product product : combo.getProducts()) {
            for (ProductStore productStore : product.getProductStores()) {
                if (productStore.getStore().getStoreId().equals(storeId)) {
                    productStore.setStockQuantity(productStore.getStockQuantity() - quantity);
                    productStoreRepository.save(productStore);
                }
            }
        }
        return ResponseEntity.ok(new APIRespone(true, "Combo quantity updated successfully", ""));
    }
    @Override
    public ResponseEntity<APIRespone> updateOrderStatusOfOwner(String orderCode, Long ownerId, String status) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order code not found", ""));
        }
        Order order = orderOptional.get();
        Store store = order.getStore();
        if (!store.getManager().getId().equals(ownerId)) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "You are not authorized to update this order", ""));
        }
        order.setStatus(status);
        orderRepository.save(order);
        return ResponseEntity.ok(new APIRespone(true, "Order status updated successfully", new OrderResponse(order)));
    }
    @Override
    public ResponseEntity<APIRespone> updateOrderStatus(String orderCode, String status) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "OrderCode not found", ""));
        }
        Order order = orderOptional.get();
        order.setStatus(status);
        orderRepository.save(order);
        return ResponseEntity.ok(new APIRespone(true, "Order status updated successfully",""));
    }
    @Override
    public ResponseEntity<APIRespone> cancelOrder(String orderCode, Long userId) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order not found", ""));
        }
        Order order = orderOptional.get();
        if (!order.getUser().getId().equals(userId)) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order does not belong to the specified user", ""));
        }
        if (order.getStatus().equals("Đơn hàng đã được xác nhận")) { // đây là k
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order cannot be canceled", ""));
        }
        order.setStatus("Đã hủy");
        orderRepository.save(order);
        return ResponseEntity.ok(new APIRespone(true, "Order canceled successfully", ""));
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
    public  ResponseEntity<APIRespone> getAllOrdersByOwner(Long ownerId) {
        List<Order> orders = orderRepository.findAll();
        List<Order> ownerOrders = orders.stream()
                .filter(order -> order.getStore().getManager().getId().equals(ownerId))
                .collect(Collectors.toList());
        List<OrderResponse> orderResponses = ownerOrders.stream().map(OrderResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", orderResponses));
    }

    @Override
    public ResponseEntity<APIRespone> getOrdersByStatusAndUserId(String status, Long userId) {
        List<Order> orders = orderRepository.findAllByStatusAndUserId(status, userId);
        if (orders.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }
        List<OrderResponse> orderResponses = orders.stream().map(OrderResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", orderResponses));
    }

    @Override
    public ResponseEntity<APIRespone> findOrderByOrderIdAndUserId(String orderCode, Long userId) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order not found", ""));
        }
        Order order = orderOptional.get();
        if (!order.getUser().getId().equals(userId)) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order does not belong to the specified user", ""));
        }
        return ResponseEntity.ok(new APIRespone(true, "Success", new OrderResponse(order)));
    }

    @Override
    public ResponseEntity<APIRespone> getOrdersByStatusAndOwnerId(String status, Long ownerId) {
        List<Order> orders = orderRepository.findAll();
        List<Order> ownerOrders = orders.stream()
                .filter(order -> order.getStore().getManager().getId().equals(ownerId))
                .filter(order -> order.getStatus().equals(status))
                .collect(Collectors.toList());
        List<OrderResponse> orderResponses = ownerOrders.stream().map(OrderResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", orderResponses));
    }

    @Override
    public ResponseEntity<APIRespone> processComboOrder(Long userId, String paymentMethod, List<Long> cartIds, String deliveryAddress, String orderCode) {
        List<Cart> cartItems = cartIds.stream()
                .flatMap(cartId -> cartItemRepository.findByCartId(cartId).stream())
                .filter(cartItem -> cartItem.getUser().getId().equals(userId))
                .collect(Collectors.toList()); // get all cart items by cartId and userId

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts are empty", ""));
        }

        if (cartItems.stream().anyMatch(cartItem -> !cartItem.getUser().getId().equals(userId))) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts does not belong to the specified you! ", ""));
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

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        User user = userOptional.get();

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(cartItems.get(0).getStatus());
        order.setOrderCode(orderCode);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setStore(store);
        order.setUser(user);
        order.setPaymentMethod(paymentMethodEntity);
        order.setDeliveryAddress(deliveryAddress);

        List<OrderDetail> orderDetails = cartItems.stream().map(cartItem -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setCombo(cartItem.getCombo());
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(cartItem.getUnitPrice());
            orderDetail.setTotalPrice(cartItem.getTotalPrice());
            orderDetail.setSize(cartItem.getSize());
            return orderDetail;
        }).collect(Collectors.toList());
        order.setOrderDetails(orderDetails);
        order.setTotalAmount(orderDetails.stream().mapToDouble(OrderDetail::getTotalPrice).sum());
        orderRepository.save(order);
        System.out.println("Đã lưu order");
        cartItemRepository.deleteAll(cartItems);
        System.out.println("Các sản phẩm đã được xóa khỏi giỏ hàng");
        return ResponseEntity.ok(new APIRespone(true, "Order placed successfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> getAllOrdersByAdmin() {
      if (orderRepository.findAll().isEmpty()) {
          return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
      }
        List<Order> orders = orderRepository.findAll();
        List<OrderResponse> orderResponses = orders.stream().map(OrderResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", orderResponses));
    }

    @Override
    public List<Cart> getCartItemsByCartId(Long cartId) {
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

    @Override
    public Order findOrderByOrderCode(String orderCode) {
        return orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with code: " + orderCode));
    }

    @Override
    public Order findOrderByOrderIdAndOwnerId(String orderCode, Long ownerId) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            return null;
        }
        Order order = orderOptional.get();
        Store store = order.getStore();
        if (!store.getManager().getId().equals(ownerId)) {
            return null;
        }
        return order;
    }



}