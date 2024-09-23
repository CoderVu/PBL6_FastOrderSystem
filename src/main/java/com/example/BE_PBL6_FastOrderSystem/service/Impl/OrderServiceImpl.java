package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.*;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.OrderResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final ComboRepository comboRepository;
    private final ProductStoreRepository productStoreRepository;
    private final SizeRepository sizeRepository;
    private final StatusOrderRepository statusOrderRepository;
    public String generateUniqueOrderCode() {
        Random random = new Random();
        String orderCode;
        do {
            orderCode = String.format("%06d", random.nextInt(900000) + 100000);
        } while (orderRepository.existsByOrderCode(orderCode));
        return orderCode;
    }

    @Override
    public ResponseEntity<APIRespone> processOrder(Long userId, String paymentMethod, List<Long> cartIds, String deliveryAddress, String orderCode) {
        List<Cart> cartItems = cartIds.stream()
                .flatMap(cartId -> cartItemRepository.findByCartId(cartId).stream())
                .filter(cartItem -> cartItem.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts are empty", ""));
        }

        if (cartItems.stream().anyMatch(cartItem -> !cartItem.getUser().getId().equals(userId))) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts does not belong to the specified user!", ""));
        }

        List<Store> stores = cartItems.stream()
                .map(Cart::getStoreId)
                .map(storeRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No stores found in the cart", ""));
        }
        PaymentMethod paymentMethodEntity = paymentMethodRepository.findByName(paymentMethod);
        if (paymentMethodEntity == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Payment method not found", ""));
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        User user = userOptional.get();

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(statusOrderRepository.findByStatusName("Đơn hàng mới"));
        order.setOrderCode(orderCode);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress);

        List<OrderDetail> orderDetails = cartItems.stream().map(cartItem -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            if (cartItem.getProduct() != null) {
                orderDetail.setProduct(cartItem.getProduct());
            } else if (cartItem.getCombo() != null) {
                orderDetail.setCombo(cartItem.getCombo());
            }
            orderDetail.setQuantity(cartItem.getQuantity());
            orderDetail.setUnitPrice(cartItem.getUnitPrice());
            orderDetail.setTotalPrice(cartItem.getTotalPrice());
            orderDetail.setSize(cartItem.getSize());
            Store store = storeRepository.findById(cartItem.getStoreId()).orElseThrow(() -> new EntityNotFoundException("Store not found"));
            orderDetail.setStore(store);
            orderDetail.setStatus(statusOrderRepository.findByStatusName("Đơn hàng mới"));
            return orderDetail;
        }).collect(Collectors.toList());
        order.setOrderDetails(orderDetails);
        order.setTotalAmount(orderDetails.stream().mapToDouble(OrderDetail::getTotalPrice).sum());

        orderRepository.save(order);
        cartItemRepository.deleteAll(cartItems);

        return ResponseEntity.ok(new APIRespone(true, "Order placed successfully", ""));
    }
    public ResponseEntity<APIRespone> processProductOrderNow(Long userId, String paymentMethod, Long productId, Long storeId, Integer quantity, String size ,String deliveryAddress, String orderCode){
        Optional<Product> productOptional = productRepository.findByProductId(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found", ""));
        }
        Product product = productOptional.get();
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (storeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        PaymentMethod paymentMethodEntity = paymentMethodRepository.findByName(paymentMethod);
        if (paymentMethodEntity == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Payment method not found", ""));
        }
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }
        User user = userOptional.get();
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        StatusOrder statusOrder = statusOrderRepository.findByStatusName("Đơn hàng mới");
        order.setStatus(statusOrder);
        order.setOrderCode(orderCode);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress);
        Size s = sizeRepository.findByName(size);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setQuantity(quantity);
        orderDetail.setUnitPrice(product.getPrice());
        orderDetail.setTotalPrice(product.getPrice() * quantity);
        orderDetail.setSize(s);
        orderDetail.setStore(storeOptional.get());
        orderDetail.setStatus(statusOrderRepository.findByStatusName("Đơn hàng mới"));
        // Use a mutable list instead of List.of(...)
        List<OrderDetail> orderDetails = new ArrayList<>();
        orderDetails.add(orderDetail);
        order.setOrderDetails(orderDetails);
        order.setTotalAmount(orderDetail.getTotalPrice());
        orderRepository.save(order);
        return ResponseEntity.ok(new APIRespone(true, "Order placed successfully", ""));
        }
    @Override
    public ResponseEntity<APIRespone> processOrderNow(Long userId, String paymentMethod, Long productId, Long comboId, Long drinkId, Long storeId, Integer quantity, String size, String deliveryAddress, String orderCode) {
        Product product = null;
        Combo combo = null;

        if (productId != null) {
            Optional<Product> productOptional = productRepository.findByProductId(productId);
            if (productOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found", ""));
            }
            product = productOptional.get();
        }

        if (comboId != null) {
            Optional<Combo> comboOptional = comboRepository.findById(comboId);
            if (comboOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
            }
            combo = comboOptional.get();
        }

        if (product == null && combo == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Neither product nor combo found", ""));
        }

        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (storeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }

        PaymentMethod paymentMethodEntity = paymentMethodRepository.findByName(paymentMethod);
        if (paymentMethodEntity == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Payment method not found", ""));
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
        }

        User user = userOptional.get();
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        StatusOrder statusOrder = statusOrderRepository.findByStatusName("Đơn hàng mới");
        order.setStatus(statusOrder);
        order.setOrderCode(orderCode);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setUser(user);
        order.setDeliveryAddress(deliveryAddress);
        Size s = sizeRepository.findByName(size);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setCombo(combo);
        orderDetail.setQuantity(quantity);

        if (product != null) {
            orderDetail.setUnitPrice(product.getPrice());
            orderDetail.setTotalPrice(product.getPrice() * quantity);
        } else if (combo != null) {
            orderDetail.setUnitPrice(combo.getComboPrice());
            orderDetail.setTotalPrice(combo.getComboPrice() * quantity);
        }

        // Thiết lập thông tin nước uống nếu có
        if (drinkId != null) {
            Optional<Product> drinkOptional = productRepository.findByProductId(drinkId);
            if (drinkOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Drink not found", ""));
            }
            if (drinkOptional.isPresent()) {
                Product drink = drinkOptional.get();
                orderDetail.setDrinkProduct(drink);
            }
        }

        orderDetail.setSize(s);
        orderDetail.setStore(storeOptional.get());
        orderDetail.setStatus(statusOrder);
        List<OrderDetail> orderDetails = new ArrayList<>();
        orderDetails.add(orderDetail);
        order.setOrderDetails(orderDetails);
        order.setTotalAmount(orderDetail.getTotalPrice());
        orderRepository.save(order);

        return ResponseEntity.ok(new APIRespone(true, "Order placed successfully", ""));
    }

    @Override
    public Long calculateOrderNowAmount(Long productId, Long comboId, int quantity) {
        if (productId != null) {
            Optional<Product> productOptional = productRepository.findByProductId(productId);
            if (productOptional.isEmpty()) {
                return null;
            }
            Product product = productOptional.get();
            return Math.round(product.getPrice() * quantity);
        }
        if (comboId != null) {
            Optional<Combo> comboOptional = comboRepository.findById(comboId);
            if (comboOptional.isEmpty()) {
                return null;
            }
            Combo combo = comboOptional.get();
            return Math.round(combo.getComboPrice() * quantity);
        }
        return null;
    }
    @Override
    public ResponseEntity<APIRespone> updateQuantityProduct(Long productId, Long comboId, Long storeId, int quantity) {
        if (productId != null) {
            Optional<ProductStore> productStoreOptional = productStoreRepository.findByProductIdAndStoreId(productId, storeId);
            if (productStoreOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found", ""));
            }
            ProductStore productStore = productStoreOptional.get();
            if (productStore.getStockQuantity() < quantity) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product not enough", ""));
            }
            if (productStore.getStockQuantity() == 0) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product out of stock", ""));
            }
            productStore.setStockQuantity(productStore.getStockQuantity() - quantity);
            productStoreRepository.save(productStore);
            return ResponseEntity.ok(new APIRespone(true, "Product quantity updated successfully", ""));
        }
        if (comboId != null) {
            Optional<Combo> comboOptional = comboRepository.findById(comboId);
            if (comboOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
            }
            Combo combo = comboOptional.get();
            List<ProductStore> productStores = combo.getProducts().stream()
                    .map(product -> productStoreRepository.findByProductIdAndStoreId(product.getProductId(), storeId))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            if (productStores.isEmpty()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found", ""));
            }
            if (productStores.stream().anyMatch(productStore -> productStore.getStockQuantity() < quantity)) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product not enough", ""));
            }
            if (productStores.stream().anyMatch(productStore -> productStore.getStockQuantity() == 0)) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Product out of stock", ""));
            }
            productStores.forEach(productStore -> productStore.setStockQuantity(productStore.getStockQuantity() - quantity));
            productStoreRepository.saveAll(productStores);
            return ResponseEntity.ok(new APIRespone(true, "Product quantity updated successfully", ""));
        }
        return ResponseEntity.badRequest().body(new APIRespone(false, "Neither product nor combo found", ""));
    }
    @Override
    public ResponseEntity<APIRespone> updateQuantityProductOrderByProduct(Long productId, Long storeId, int quantity) {
        Optional<ProductStore> productOptional = productStoreRepository.findByProductIdAndStoreId(productId, storeId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found", ""));
        }
        ProductStore productStore = productOptional.get();
        if (productStore.getStockQuantity() < quantity) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product not enough", ""));
        }
        if (productStore.getStockQuantity() == 0) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product out of stock", ""));
        }
        productStore.setStockQuantity(productStore.getStockQuantity() - quantity);
        productStoreRepository.save(productStore);
        return ResponseEntity.ok(new APIRespone(true, "Product quantity updated successfully", ""));
    }

    @Override
    @Transactional
    public ResponseEntity<APIRespone> updateQuantityProductOrderByCombo(Long comboId, Long storeId, int quantity) {
        System.out.println("vao updateQuantityProductOrderByCombo");
        Optional<Combo> comboOptional = comboRepository.findById(comboId);
        if (comboOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Combo not found", ""));
        }
        Combo combo = comboOptional.get();
        List<ProductStore> productStores = combo.getProducts().stream()
                .map(product -> productStoreRepository.findByProductIdAndStoreId(product.getProductId(), storeId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        if (productStores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found", ""));
        }
        if (productStores.stream().anyMatch(productStore -> productStore.getStockQuantity() < quantity)) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product not enough", ""));
        }
        if (productStores.stream().anyMatch(productStore -> productStore.getStockQuantity() == 0)) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product out of stock", ""));
        }
        productStores.forEach(productStore -> productStore.setStockQuantity(productStore.getStockQuantity() - quantity));
        productStoreRepository.saveAll(productStores);
        return ResponseEntity.ok(new APIRespone(true, "Product quantity updated successfully", ""));
    }
    @Override
    public ResponseEntity<APIRespone> updateOrderStatus(String orderCode,String status) {
        System.out.println("vao updateOrderStatus");
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order code not found", ""));
        }
        StatusOrder statusOrder = statusOrderRepository.findByStatusName(status);
        if (statusOrder == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status not found", ""));
        }
        Order order = orderOptional.get();
        order.setStatus(statusOrder);
        orderRepository.save(order);
        return ResponseEntity.ok(new APIRespone(true, "Order status updated successfully", new OrderResponse(order)));
    }

    @Override
    public ResponseEntity<APIRespone> getAllOrderDetailOfStore(Long ownerId) {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }
        List<Store> stores = storeRepository.findAllByManagerId(ownerId);
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        List<Order> orders1 = orders.stream()
                .filter(order -> order.getOrderDetails().stream().anyMatch(orderDetail -> stores.contains(orderDetail.getStore())))
                .collect(Collectors.toList());
        if (orders1.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }
        List<OrderResponse> orderResponses = orders1.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", orderResponses));
    }
    @Override
    public ResponseEntity<APIRespone> getOrderDetailOfStore(Long ownerId, String orderCode) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order code not found", ""));
        }
        Order order = orderOptional.get();
        List<Store> stores = storeRepository.findAllByManagerId(ownerId);
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        if (order.getOrderDetails().stream().noneMatch(orderDetail -> stores.contains(orderDetail.getStore()))) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order does not belong to the specified store", ""));
        }
        return ResponseEntity.ok(new APIRespone(true, "Success", new OrderResponse(order)));
    }
    @Override
    public ResponseEntity<APIRespone> getOrderDetailByUserId(Long userId, String orderCode) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order code not found", ""));
        }
        Order order = orderOptional.get();
        if (!order.getUser().getId().equals(userId)) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order does not belong to the specified user", ""));
        }
        if (order.getOrderDetails().stream().noneMatch(orderDetail -> order.getUser().getId().equals(userId))) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order does not belong to the specified user", ""));
        }
        return ResponseEntity.ok(new APIRespone(true, "Success", new OrderResponse(order)));
    }

    @Override
    public ResponseEntity<APIRespone> updateStatusDetail(String orderCode, Long OwnerId, String Status) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order code not found", ""));
        }
        // update status order detail
        Order order = orderOptional.get();
        List<OrderDetail> orderDetails = order.getOrderDetails();
        StatusOrder statusOrder = statusOrderRepository.findByStatusName(Status);
        if (statusOrder == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status not found", ""));
        }
        // tim tat ca store cua StoreId
        List<Store> stores = storeRepository.findAllByManagerId(OwnerId);
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        if (orderDetails.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order detail not found", ""));
        }
        // tim tat ca order detail cua store
        List<OrderDetail> orderDetails1 = orderDetails.stream()
                .filter(orderDetail -> stores.contains(orderDetail.getStore()))
                .collect(Collectors.toList());
        if (orderDetails1.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order detail not found", ""));
        }
        orderDetails1.forEach(orderDetail -> orderDetail.setStatus(statusOrder));
        orderDetailRepository.saveAll(orderDetails1);
        orderRepository.save(order);
        return ResponseEntity.ok(new APIRespone(true, "Status OrderDetail updated successfully", ""));
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
        if (order.getStatus().equals("Đơn hàng đã được xác nhận")) { 
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order cannot be canceled", ""));
        }
        order.setStatus(statusOrderRepository.findByStatusName("Đơn hàng đã bị hủy"));
        orderRepository.save(order);
        return ResponseEntity.ok(new APIRespone(true, "Order canceled successfully", ""));
    }

    @Override
    public ResponseEntity<APIRespone> getOrdersByStatusAndUserId(String statusName, Long userId) {
        StatusOrder statusOrder = statusOrderRepository.findByStatusName(statusName);
        if (statusOrder == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status not found", ""));
        }
        List<Order> orders = orderRepository.findAllByStatusAndUserId(statusOrder, userId);
        if (orders.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No orders found for the specified status and user", ""));
        }
        List<OrderResponse> orderResponses = orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", orderResponses));
    }
    @Override
    public ResponseEntity<APIRespone> findOrderByOrderCodeAndUserId(String orderCode, Long userId) {
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
    public List<Cart> getCartItemsByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }
    @Transactional
    @Override
    public ResponseEntity<APIRespone> findOrderByOrderCode(String orderCode) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order not found", ""));
        }
        Order order = orderOptional.get();
        return ResponseEntity.ok(new APIRespone(true, "Success", new OrderResponse(order)));
    }
    @Override
    public ResponseEntity<APIRespone> getAllOrderDetailsByUser(Long userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }
        List<OrderResponse> orderResponses = orders.stream().map(OrderResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", orderResponses));
    }
    

}