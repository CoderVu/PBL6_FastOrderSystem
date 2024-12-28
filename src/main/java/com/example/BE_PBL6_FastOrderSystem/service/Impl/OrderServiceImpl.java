package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.entity.*;
import com.example.BE_PBL6_FastOrderSystem.repository.*;
import com.example.BE_PBL6_FastOrderSystem.response.*;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private final ShipperRepository shipperRepository;
    private final ShippingFeeRepository shippingFeeRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final AnnounceRepository announceRepository;
    private final VoucherRepository discountCodeRepository;
    private final PaymentRepository paymentRepository;

    public OrderResponse getOrderResponse(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            Optional<Payment> paymentOptional = paymentRepository.findByOrderCode(order.getOrderCode());
            String paymentMethod = paymentOptional.map(payment -> payment.getPaymentMethod().getName()).orElse("Unknown");
            String statusPayment = paymentOptional.map(Payment::getStatus).orElse("Unknown");
            return new OrderResponse(order, paymentMethod, statusPayment);
        } else {
            throw new RuntimeException("Order not found");
        }
    }
    public String generateUniqueOrderCode() {
        Random random = new Random();
        String orderCode;
        do {
            orderCode = String.format("%06d", random.nextInt(900000) + 100000);
        } while (orderRepository.existsByOrderCode(orderCode));
        return orderCode;
    }
    @Override
    public Double getPriceBasedProductOnSize(Product product, Size size) {
        Double basePrice = (product.getDiscountedPrice() != 0.0) ? product.getDiscountedPrice() : product.getPrice();
        return switch (size.getName()) {
            case "L" -> basePrice + 10000;
            case "XL" -> basePrice + 20000;
            default -> basePrice;
        };
    }
    @Override
    public Double getPriceBasedComboOnSize(Combo combo, Size size) {
        Double basePrice = combo.getComboPrice();
        return switch (size.getName()) {
            case "L" -> basePrice + 10000;
            case "XL" -> basePrice + 20000;
            default -> basePrice;
        };
    }
    @Override
    public ResponseEntity<APIRespone> findNearestShipper(Double latitude, Double longitude, int limit) {
        List<User> nearestShippers = shipperRepository.findNearestShippers(latitude, longitude, limit);
        if (nearestShippers.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No shippers found nearby", ""));
        }
        List<UserResponse> userResponses = nearestShippers.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", userResponses));
    }
    @Transactional
    @Override
    public ResponseEntity<APIRespone> processOrder(Long userId, String paymentMethod, List<Long> cartIds, String deliveryAddress, Double longitude, Double latitude, String orderCode, String discountCode) {
        List<Cart> cartItems = cartIds.stream()
                .flatMap(cartId -> cartItemRepository.findByCartId(cartId).stream())
                .filter(cartItem -> cartItem.getUser().getId().equals(userId))
                .collect(Collectors.toList());

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts are empty", ""));
        }

        if (cartItems.stream().anyMatch(cartItem -> !cartItem.getUser().getId().equals(userId))) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Carts do not belong to the specified user!", ""));
        }

        List<Store> stores = cartItems.stream()
                .map(Cart::getStoreId)
                .map(storeRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

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
        order.setOrderDate(ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDateTime());
        StatusOrder statusOrder = statusOrderRepository.findByStatusName("Đơn hàng mới");
        order.setStatus(statusOrder);
        order.setOrderCode(orderCode);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setUser(user);
        order.setFeedback(false);
        if (discountCode != null) {
            Optional<Voucher> voucherOptional = discountCodeRepository.findByCode(discountCode);
            if (voucherOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Discount code not found", ""));
            }
            Voucher voucher = voucherOptional.get();
            order.setVoucher(voucher);
        }
        List<OrderDetail> orderDetails = cartItems.stream().map(cartItem -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            if (cartItem.getProduct() != null) {
                orderDetail.setProduct(cartItem.getProduct());
            } else if (cartItem.getCombo() != null) {
                orderDetail.setCombo(cartItem.getCombo());
            }
            orderDetail.setQuantity(Integer.valueOf(cartItem.getQuantity()));
            orderDetail.setUnitPrice(cartItem.getUnitPrice());
            orderDetail.setTotalPrice(cartItem.getTotalPrice());
            orderDetail.setSize(cartItem.getSize());
            Store store = storeRepository.findById(cartItem.getStoreId()).orElseThrow(() -> new EntityNotFoundException("Store not found"));
            orderDetail.setStore(store);
            orderDetail.setStatus(statusOrder);

            if (cartItem.getDrinkProducts() != null) {
                List<Product> drinkProducts = new ArrayList<>(cartItem.getDrinkProducts());
                orderDetail.setDrinkProducts(drinkProducts);
            }
            return orderDetail;
        }).collect(Collectors.toList());
        // Separate handling for in-store vs. online purchase
        if (deliveryAddress.equalsIgnoreCase("Mua tại cửa hàng")) {
            // In-store purchase logic
            order.setDeliveryAddress("Mua tại cửa hàng");
            Long totalAmountInStore = TinhTongTienKhiMuaTaiQuay(cartIds, discountCode);
            if (totalAmountInStore == null) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Failed to calculate total amount for in-store purchase", ""));
            }
            order.setTotalAmount(Double.valueOf(totalAmountInStore));
            System.out.println("Total amount for in-store purchase: " + totalAmountInStore);
        } else {
            // Online purchase logic
            order.setDeliveryAddress(deliveryAddress);
            order.setLongitude(longitude);
            order.setLatitude(latitude);

            Map<Store, List<OrderDetail>> groupedOrderDetails = orderDetails.stream()
                    .collect(Collectors.groupingBy(OrderDetail::getStore));
            for (Map.Entry<Store, List<OrderDetail>> entry : groupedOrderDetails.entrySet()) {
                Store store = entry.getKey();
                List<OrderDetail> storeOrderDetails = entry.getValue();

                Double shippingFee = calculateShippingFee(order, store);
                ShippingFee fee = new ShippingFee();
                fee.setFee(shippingFee);
                shippingFeeRepository.save(fee);

                for (OrderDetail orderDetail : storeOrderDetails) {
                    orderDetail.setShippingFee(fee);
                }
            }

            Long totalAmountOnline = calculateOrderAmount(cartIds, latitude, longitude, discountCode);
            if (totalAmountOnline == null) {
                return ResponseEntity.badRequest().body(new APIRespone(false, "Failed to calculate total amount for online purchase", ""));
            }
            order.setTotalAmount(Double.valueOf(totalAmountOnline));
            System.out.println("Total amount for online purchase: " + totalAmountOnline);
        }

        order.setOrderDetails(orderDetails);
        orderRepository.save(order);

        if (discountCode != null) {
            UserVoucher userVoucher = userVoucherRepository.findByCode(discountCode, userId);
            if (userVoucher != null) {
                userVoucher.setIsUsed(true);
                userVoucherRepository.save(userVoucher);
            }
        }

        AnnounceUser announceUser = new AnnounceUser(userId, "Order Notification", "Your order " + orderCode + " has been successfully placed. Total value " + order.getTotalAmount());
        announceRepository.save(announceUser);

        for (Store store : stores) {
            User owner = store.getManager();
            AnnounceUser storeAnnounceUser = new AnnounceUser(owner.getId(), "Order Confirmation Notification", "A new order with code " + orderCode + " has been placed.");
            announceRepository.save(storeAnnounceUser);
        }

        return ResponseEntity.ok(new APIRespone(true, "Order placed successfully", ""));
    }

@Transactional
@Override
public ResponseEntity<APIRespone> processOrderNow(Long userId, String paymentMethod, Long productId, Long comboId, List<Long> drinkId, Long storeId, Integer quantity, String size, String deliveryAddress, Double longitude, Double latitude, String orderCode, String discountCode) {
    Product product = null;
    Combo combo = null;

    // Validate product and combo
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

    // Validate store and payment method
    Optional<Store> storeOptional = storeRepository.findById(storeId);
    if (storeOptional.isEmpty()) {
        return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
    }

    PaymentMethod paymentMethodEntity = paymentMethodRepository.findByName(paymentMethod);
    if (paymentMethodEntity == null) {
        return ResponseEntity.badRequest().body(new APIRespone(false, "Payment method not found", ""));
    }

    // Calculate total amount
    Long totalAmount;
    if ("Mua tại cửa hàng".equalsIgnoreCase(deliveryAddress)) {
        totalAmount = TinhTongTienKhiMuaNgayTaiQuay(productId, comboId, quantity, size);
    } else {
        totalAmount = calculateOrderNowAmount(productId, comboId, quantity, storeId, latitude, longitude, discountCode, size);

    }

    if (totalAmount == null) {
        return ResponseEntity.badRequest().body(new APIRespone(false, "Failed to calculate total amount", ""));
    }

    // Validate user
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isEmpty()) {
        return ResponseEntity.badRequest().body(new APIRespone(false, "User not found", ""));
    }
    User user = userOptional.get();

    // Set up the order
    Order order = new Order();
    order.setOrderDate(LocalDateTime.now());
    StatusOrder statusOrder = statusOrderRepository.findByStatusName("Đơn hàng mới");
    order.setStatus(statusOrder);
    order.setOrderCode(orderCode);
    order.setCreatedAt(LocalDateTime.now());
    order.setUpdatedAt(LocalDateTime.now());
    order.setUser(user);
    order.setFeedback(false);
    order.setTotalAmount(Double.valueOf(totalAmount));
    if (discountCode != null) {
        Optional<Voucher> voucherOptional = discountCodeRepository.findByCode(discountCode);
        if (voucherOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Discount code not found", ""));
        }
        Voucher voucher = voucherOptional.get();
        order.setVoucher(voucher);
    }

    // Set up order details
    OrderDetail orderDetail = new OrderDetail();
    orderDetail.setOrder(order);
    orderDetail.setProduct(product);
    orderDetail.setCombo(combo);
    orderDetail.setQuantity(quantity);
    Size s = sizeRepository.findByName(size);
    orderDetail.setSize(s);
    orderDetail.setStore(storeOptional.get());
    orderDetail.setStatus(statusOrder);

    // Calculate unit price and total price based on product/combo
    if (product != null) {
        Double unitPrice = getPriceBasedProductOnSize(product, s);
        orderDetail.setUnitPrice(unitPrice);
        Double totalPrice = unitPrice * quantity;
        if (discountCode != null) {
            totalPrice = applyDiscount(totalPrice, discountCode);
        }
        orderDetail.setTotalPrice(totalPrice);
    } else {
        Double unitPrice = getPriceBasedComboOnSize(combo, s);
        orderDetail.setUnitPrice(unitPrice);
        Double totalPrice = unitPrice * quantity;
        if (discountCode != null) {
            totalPrice = applyDiscount(totalPrice, discountCode);
        }
        orderDetail.setTotalPrice(totalPrice);
    }
    // Set delivery address and shipping if applicable
    if ("Mua tại cửa hàng".equalsIgnoreCase(deliveryAddress)) {
        order.setDeliveryAddress("Mua tại cửa hàng");
    } else {
        order.setDeliveryAddress(deliveryAddress);
        order.setLongitude(longitude);
        order.setLatitude(latitude);

        // Group order details by store and calculate shipping fee
        List<OrderDetail> orderDetails = List.of(orderDetail);
        Map<Store, List<OrderDetail>> groupedOrderDetails = orderDetails.stream()
                .collect(Collectors.groupingBy(OrderDetail::getStore));
        for (Map.Entry<Store, List<OrderDetail>> entry : groupedOrderDetails.entrySet()) {
            Store store = entry.getKey();
            List<OrderDetail> storeOrderDetails = entry.getValue();
            Double shippingFee = calculateShippingFee(order, store);
            ShippingFee fee = new ShippingFee();
            fee.setFee(shippingFee);
            shippingFeeRepository.save(fee);
            for (OrderDetail od : storeOrderDetails) {
                od.setShippingFee(fee);
            }
        }
    }

    // Save the order and send notifications
    order.setOrderDetails(List.of(orderDetail));
    orderRepository.save(order);

    if (discountCode != null) {
        UserVoucher userVoucher = userVoucherRepository.findByCode(discountCode, userId);
        if (userVoucher != null) {
            userVoucher.setIsUsed(true);
            userVoucherRepository.save(userVoucher);
        }
    }

    AnnounceUser announceUser = new AnnounceUser(userId, "Thông báo đơn hàng", "Bạn đặt hàng " + orderCode + " thành công. Tổng giá trị " + totalAmount);
    announceRepository.save(announceUser);
    User owner = storeOptional.get().getManager();
    AnnounceUser announceUser1 = new AnnounceUser(owner.getId(), "Thông báo xác nhận đơn hàng", "Có đơn hàng mới có mã " + orderCode);
    announceRepository.save(announceUser1);

    return ResponseEntity.ok(new APIRespone(true, "Order placed successfully", ""));
}


    private Double applyDiscount(Double totalPrice, String discountCode) {
        Voucher voucher = discountCodeRepository.findByCode(discountCode).orElseThrow();
        return totalPrice - totalPrice * voucher.getDiscountPercent() / 100;
    }

    @Override
    public Double calculateShippingFee(Order order, Store store) {
        double storeLatitude = store.getLatitude();
        double storeLongitude = store.getLongitude();
        double deliveryLatitude = order.getLatitude();
        double deliveryLongitude = order.getLongitude();
        final int EARTH_RADIUS = 6371;
        double latDistance = Math.toRadians(deliveryLatitude - storeLatitude);
        double lonDistance = Math.toRadians(deliveryLongitude - storeLongitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(storeLatitude)) * Math.cos(Math.toRadians(deliveryLatitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        System.out.println("Distance: " + distance);
        double shippingFeePerKm = 10000;
        if (distance <= 0.1) {
            return 0.0;
        }
        double shippingFee = distance * shippingFeePerKm;
        double shippingFeeRounded = Math.floor(shippingFee / 1000) * 1000;
        System.out.println("Shipping fee: " + shippingFeeRounded);
        return shippingFeeRounded;

    }

    @Override
    public Long calculateOrderNowAmount(Long productId, Long comboId, int quantity, Long storeId, Double latitude, Double longitude, String discountCode, String size) {
        long totalAmount = 0;
        // Tính tổng số tiền từ sản phẩm
        if (productId != null) {
            Optional<Product> productOptional = productRepository.findByProductId(productId);
            if (productOptional.isEmpty()) {
                return null;
            }
            Product product = productOptional.get();
            Size s = sizeRepository.findByName(size);
            Double unitPrice = getPriceBasedProductOnSize(product, s);
            totalAmount += Math.round(unitPrice * quantity);
        }

        // Tính tổng số tiền từ combo
        if (comboId != null) {
            Optional<Combo> comboOptional = comboRepository.findById(comboId);
            if (comboOptional.isEmpty()) {
                return null;
            }
            Combo combo = comboOptional.get();
            Size s = sizeRepository.findByName(size);
            Double unitPrice = getPriceBasedComboOnSize(combo, s);
            totalAmount += Math.round(unitPrice * quantity);
        }

        // Kiểm tra mã giảm giá
        if (discountCode != null) {
            Voucher voucher = discountCodeRepository.findByCode(discountCode).orElseThrow();
            totalAmount -= Math.round(totalAmount * voucher.getDiscountPercent() / 100);
        }

        // Tính phí giao hàng
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (storeOptional.isPresent()) {
            Store store = storeOptional.get();
            Order dummyOrder = new Order();
            dummyOrder.setLatitude(latitude);
            dummyOrder.setLongitude(longitude);

            Double shippingFee = calculateShippingFee(dummyOrder, store);
            totalAmount += Math.round(shippingFee);
        } else {
            return null;
        }
        return totalAmount;
    }
    @Override
    public Long TinhTongTienKhiMuaNgayTaiQuay(Long productId, Long comboId, int quantity,String size) {
        long totalAmount = 0;
        // Tính tổng số tiền từ sản phẩm
        if (productId != null) {
            Optional<Product> productOptional = productRepository.findByProductId(productId);
            if (productOptional.isEmpty()) {
                return null;
            }
            Product product = productOptional.get();
            Size s = sizeRepository.findByName(size);
            Double unitPrice = getPriceBasedProductOnSize(product, s);
            totalAmount += Math.round(unitPrice * quantity);
        }

        // Tính tổng số tiền từ combo
        if (comboId != null) {
            Optional<Combo> comboOptional = comboRepository.findById(comboId);
            if (comboOptional.isEmpty()) {
                return null;
            }
            Combo combo = comboOptional.get();
            Size s = sizeRepository.findByName(size);
            Double unitPrice = getPriceBasedComboOnSize(combo, s);
            totalAmount += Math.round(unitPrice * quantity);
        }

        return totalAmount;
    }
    @Override
    public Long calculateOrderAmount(List<Long> cartIds, Double latitude, Double longitude, String discountCode) {
        List<Cart> cartItems = cartIds.stream()
                .flatMap(cartId -> getCartItemsByCartId(cartId).stream())
                .toList();

        long totalAmount = 0;
        for (Cart item : cartItems) {
            totalAmount += Math.round(item.getTotalPrice());
        }
        System.out.println("Total amount trước khi tính toán ở cart: " + totalAmount);

        // Kiểm tra mã giảm giá
        if (discountCode != null) {
            Voucher voucher = discountCodeRepository.findByCode(discountCode).orElseThrow();
            totalAmount -= Math.round(totalAmount * voucher.getDiscountPercent() / 100);
            System.out.println("Total amount sau khi giảm giá: " + totalAmount);
        }

        // Group cart items by store
        Map<Long, List<Cart>> itemsByStore = cartItems.stream()
                .collect(Collectors.groupingBy(Cart::getStoreId));


        // Calculate shipping fee for each store
        double totalShippingFee = 0;
        for (Map.Entry<Long, List<Cart>> entry : itemsByStore.entrySet()) {
            Long storeId = entry.getKey();
            Optional<Store> storeOptional = storeRepository.findById(storeId);
            if (storeOptional.isPresent()) {
                Store store = storeOptional.get();
                Order dummyOrder = new Order();
                dummyOrder.setLatitude(latitude);
                dummyOrder.setLongitude(longitude);
                Double shippingFee = calculateShippingFee(dummyOrder, store);
                totalShippingFee += Math.round(shippingFee);
            } else {
                System.out.println("Store not found for storeId: " + storeId);
                return 0L;
            }
        }

        totalAmount += totalShippingFee;

        System.out.println("Total amount sau khi tính toán: " + totalAmount);
        System.out.println("Discount code: " + discountCode);
        return totalAmount;
    }

    @Override
    public Long TinhTongTienKhiMuaTaiQuay(List<Long> cartIds, String discountCode) {
        List<Cart> cartItems = cartIds.stream()
                .flatMap(cartId -> getCartItemsByCartId(cartId).stream())
                .toList();

        long totalAmount = 0;
        for (Cart item : cartItems) {
            totalAmount += Math.round(item.getTotalPrice());
        }
        System.out.println("Total amount trước khi tính toán ở cart: " + totalAmount);

        // Kiểm tra mã giảm giá
        if (discountCode != null) {
            Voucher voucher = discountCodeRepository.findByCode(discountCode).orElseThrow();
            totalAmount -= Math.round(totalAmount * voucher.getDiscountPercent() / 100);
            System.out.println("Total amount sau khi giảm giá: " + totalAmount);
        }
        return totalAmount;
    }


    @Transactional
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
    @Transactional
    @Override
    public ResponseEntity<APIRespone> updateOrderStatus(String orderCode,String status) {
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
        return ResponseEntity.ok(new APIRespone(true, "Order status updated successfully",""));
    }
    @Override
    public ResponseEntity<APIRespone> updateOrderDetailStatus(String orderCode, Long storeId, String status) {
        Optional<Order> orderOptional = orderRepository.findByOrderCode(orderCode);
        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order code not found", ""));
        }
        Order order = orderOptional.get();
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_OrderCode_AndStoreId(orderCode, storeId);
        if (orderDetails.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order detail not found", ""));
        }
        StatusOrder statusOrder = statusOrderRepository.findByStatusName(status);
        if (statusOrder == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status not found", ""));
        }
        orderDetails.forEach(orderDetail -> orderDetail.setStatus(statusOrder));
        orderDetailRepository.saveAll(orderDetails);

        // Check if all order details have the status "Đơn hàng đã hoàn thành"
        StatusOrder completedStatus = statusOrderRepository.findByStatusName("Đơn hàng đã hoàn thành");
        boolean allCompleted = orderDetails.stream()
                .allMatch(orderDetail -> orderDetail.getStatus().equals(completedStatus));

        if (allCompleted) {
            order.setStatus(completedStatus);
            orderRepository.save(order);
        }

        return ResponseEntity.ok(new APIRespone(true, "Order detail status updated successfully", ""));

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

        // Kiểm tra xem có đơn hàng nào có chi tiết hợp lệ không
        List<Order> orders1 = orders.stream()
                .filter(order -> {
                    if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                        return order.getOrderDetails().stream()
                                .anyMatch(orderDetail -> orderDetail.getStore() != null && stores.contains(orderDetail.getStore()));
                    }
                    // Không in ra order nếu không có OrderDetail
                    return false;
                })
                .toList();

        if (orders1.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }

        // Chuyển đổi các đơn hàng thành OrderResponse
        List<OrderResponse> orderResponses = orders1.stream()
                .map(order -> {
                    Optional<Payment> paymentOptional = paymentRepository.findByOrderCode(order.getOrderCode());
                    String paymentMethod = paymentOptional.map(payment -> payment.getPaymentMethod().getName()).orElse("Unknown");
                    String statusPayment = paymentOptional.map(Payment::getStatus).orElse("Unknown");
                    return new OrderResponse(order, paymentMethod, statusPayment);
                })
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
        Optional<Payment> paymentOptional = paymentRepository.findByOrderCode(order.getOrderCode());
        String paymentMethod = paymentOptional.map(payment -> payment.getPaymentMethod().getName()).orElse("Unknown");
        String statusPayment = paymentOptional.map(Payment::getStatus).orElse("Unknown");

        return ResponseEntity.ok(new APIRespone(true, "Success", new OrderResponse(order, paymentMethod, statusPayment)));
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
        // update status order
        order.setStatus(statusOrder);

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
                .map(order -> {
                    Optional<Payment> paymentOptional = paymentRepository.findByOrderCode(order.getOrderCode());
                    String paymentMethod = paymentOptional.map(payment -> payment.getPaymentMethod().getName()).orElse("Unknown");
                    String statusPayment = paymentOptional.map(Payment::getStatus).orElse("Unknown");
                    return new OrderResponse(order, paymentMethod, statusPayment);
                })
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
        Optional<Payment> paymentOptional = paymentRepository.findByOrderCode(orderCode);
        String paymentMethod = paymentOptional.map(payment -> payment.getPaymentMethod().getName()).orElse("Unknown");
        String statusPayment = paymentOptional.map(Payment::getStatus).orElse("Unknown");
        return ResponseEntity.ok(new APIRespone(true, "Success", new OrderResponse(order, paymentMethod, statusPayment)));

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
        Optional<Payment> paymentOptional = paymentRepository.findByOrderCode(orderCode);
        String paymentMethod = paymentOptional.map(payment -> payment.getPaymentMethod().getName()).orElse("Unknown");
        String statusPayment = paymentOptional.map(Payment::getStatus).orElse("Unknown");
        return ResponseEntity.ok(new APIRespone(true, "Success", new OrderResponse(order, paymentMethod, statusPayment)));
    }


    @Override
    public ResponseEntity<APIRespone> getAllOrderDetailsByUser(Long userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        System.out.println("Lenght order "+  orders.size());
        if (orders.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }
        List<OrderResponse> orderResponses = orders.stream()
                .map(order -> {
                    Optional<Payment> paymentOptional = paymentRepository.findByOrderCode(order.getOrderCode());
                    String paymentMethod = paymentOptional.map(payment -> payment.getPaymentMethod().getName()).orElse("Unknown");
                    String statusPayment = paymentOptional.map(Payment::getStatus).orElse("Unknown");
                    return new OrderResponse(order, paymentMethod, statusPayment);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new APIRespone(true, "Success", orderResponses));
    }
//@Override
//public ResponseEntity<APIRespone> getAllOrderDetailsByUser(Long userId, int page, int size) {
//    Pageable pageable = PageRequest.of(page, size);
//    Page<Order> ordersPage = orderRepository.findAllByUserId(userId, pageable);
//
//    if (ordersPage.isEmpty()) {
//        return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
//    }
//
//    List<OrderResponse> orderResponses = ordersPage.getContent().stream()
//            .map(order -> {
//                try {
//                    Optional<Payment> paymentOptional = paymentRepository.findByOrderCode(order.getOrderCode());
//                    String paymentMethod = paymentOptional.map(payment -> payment.getPaymentMethod().getName()).orElse("Unknown");
//                    String statusPayment = paymentOptional.map(Payment::getStatus).orElse("Unknown");
//                    return new OrderResponse(order, paymentMethod, statusPayment);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            })
//            .filter(Objects::nonNull)
//            .collect(Collectors.toList());
//
//    return ResponseEntity.ok(new APIRespone(true, "Success", orderResponses, ordersPage.getTotalPages(), ordersPage.getTotalElements()));
//}

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
        Optional<Payment> paymentOptional = paymentRepository.findByOrderCode(orderCode);
        String paymentMethod = paymentOptional.map(payment -> payment.getPaymentMethod().getName()).orElse("Unknown");
        String statusPayment = paymentOptional.map(Payment::getStatus).orElse("Unknown");
        return ResponseEntity.ok(new APIRespone(true, "Success", new OrderResponse(order, paymentMethod, statusPayment)));
    }

    @Override
    public ResponseEntity<APIRespone> getAllOrderByStatusOfStore(String statusName, Long ownerId) {
        StatusOrder statusOrder = statusOrderRepository.findByStatusName(statusName);
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(true, "No order found", ""));
        }
        List<Store> stores = storeRepository.findAllByManagerId(ownerId);
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }

        // Kiểm tra xem có đơn hàng nào có chi tiết hợp lệ không
        List<Order> orders1 = orders.stream()
                .filter(order -> {
                    if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                        return order.getOrderDetails().stream()
                                .anyMatch(orderDetail -> orderDetail.getStore() != null && stores.contains(orderDetail.getStore()));
                    }
                    // Không in ra order nếu không có OrderDetail
                    return false;
                })
                .toList();

        if (orders1.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }
        Store store = stores.get(0);
        List<OrderStore> orderStores = new ArrayList<>();
        for (Order order : orders1) {
            if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                Optional<List<OrderDetail>> orderDetails = orderRepository.findOrderDetailsByOrderCodeAndStore(order.getOrderCode(), store.getStoreId());
                List<OrderDetail> orderDetailList = orderDetails.get();
                OrderStore os = new OrderStore(orderDetailList);
                orderStores.add(os);
            }
        }


        orderStores.removeIf(os -> !os.getStatus().equals(statusName));

        System.out.println(orderStores.size());
        return ResponseEntity.ok(new APIRespone(true, "Success", orderStores));
    }

    @Override
    public ResponseEntity<APIRespone> getOrderByStatus(Long ownerId, String status) {
        StatusOrder statusOrder = statusOrderRepository.findByStatusName(status);
        if (statusOrder == null) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Status not found", ""));
        }
        List<Order> orders = orderRepository.findAllByStatus(statusOrder);
        if (orders.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }
        List<Store> stores = storeRepository.findAllByManagerId(ownerId);
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        List<Order> orders1 = orders.stream()
                .filter(order -> order.getOrderDetails().stream().anyMatch(orderDetail -> stores.contains(orderDetail.getStore())))
                .toList();
        if (orders1.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }
        List<OrderResponse> orderResponses = orders1.stream()
                .map(order -> {
                    Optional<Payment> paymentOptional = paymentRepository.findByOrderCode(order.getOrderCode());
                    String paymentMethod = paymentOptional.map(payment -> payment.getPaymentMethod().getName()).orElse("Unknown");
                    String statusPayment = paymentOptional.map(Payment::getStatus).orElse("Unknown");
                    return new OrderResponse(order, paymentMethod, statusPayment);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(new APIRespone(true, "Success", orderResponses));
    }


    @Override
    public ResponseEntity<APIRespone> updateStatusFeedBack(Long orderid){
        Order order = orderRepository.findById(orderid).get();
        order.setFeedback(true);
        orderRepository.save(order);
        return ResponseEntity.ok(new APIRespone(true, "Success", ""));
    }

    @Override
    public ResponseEntity<APIRespone> getAllTotalAmountOrder() {
        Long totalAmount = orderRepository.getTotalAmountForCompletedOrders();
        return ResponseEntity.ok(new APIRespone(true, "Success", totalAmount));
    }

    @Override
    public ResponseEntity<APIRespone> getCountOrderByMonth() {
        LocalDateTime ldt = LocalDateTime.now();
        Long totalAmount = orderRepository.countOrdersByMonth(ldt.getMonthValue(), ldt.getYear());
        return ResponseEntity.ok(new APIRespone(true, "Success", totalAmount));

    }

    @Override
    public ResponseEntity<APIRespone> getTotalAmountByMonth(int year) {
        Map<String, Long> map = new LinkedHashMap<>();

        for (int i = 0; i < 12; i++) {
            // Lùi tháng
            int month = i + 1;
            Long totalAmount = orderRepository.getTotalAmountByMonth(month, year);
            map.put("Tháng " + month + " Năm " + year, totalAmount);
        }

        return ResponseEntity.ok(new APIRespone(true, "Success", map));
    }

    @Override
    public ResponseEntity<APIRespone> getTotalAmountByWeek() {
        LocalDateTime ldt = LocalDateTime.now();
        int currentMonth = ldt.getMonthValue();
        int currentYear = ldt.getYear();
        int currentDay = ldt.getDayOfMonth();
        Map<String, Long> map = new LinkedHashMap<>();

        for (int i = 0; i < 7; i++) {
            int day = currentDay - i;
            int month = currentMonth;
            int year = currentYear;
            if (day < 1) {
                day += 7;
                month -= 1;
                if (month < 1) {
                    month += 12;
                    year -= 1;
                }
            }
            Long totalAmount = orderRepository.getTotalAmountByWeek(day, month, year);
            map.put("Ngày " + day + " Tháng " + month + " Năm " + year, totalAmount);
        }
        return ResponseEntity.ok(new APIRespone(true, "Success", map));
    }

    @Override
    public ResponseEntity<APIRespone> getCountProductSole(String module) {
        LocalDateTime ldt = LocalDateTime.now();
        int currentMonth = ldt.getMonthValue();
        int currentYear = ldt.getYear();
        int currentDay = ldt.getDayOfMonth();
        List<Product> products = productRepository.findAll();
        Map<String, Long> map = new LinkedHashMap<>();
        if (module.equals("day")) {
            System.out.println("module: day");
            for (Product product : products) {
                Long q = orderDetailRepository.getCountProductSoldDay(currentDay, currentMonth, currentYear, product.getProductId());
                if (q != null)
                    map.put(product.getProductName(), q);
            }
            // Sắp xếp map theo giá trị giảm dần và lấy 10 phần tử đầu tiên
            map = map.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // Sắp xếp theo giá trị giảm dần
                    .limit(10) // Giới hạn 10 phần tử đầu tiên
                    .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
            return ResponseEntity.ok(new APIRespone(true, "Success", map));

        } else if (module.equals("month")) {
            System.out.println("module: month");
            for (Product product : products) {
                Long q = orderDetailRepository.getCountProductSoldMonth(currentMonth, currentYear, product.getProductId());
                if (q != null)
                    map.put(product.getProductName(), q);
            }
            // Sắp xếp map theo giá trị giảm dần và lấy 10 phần tử đầu tiên
            map = map.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // Sắp xếp theo giá trị giảm dần
                    .limit(10) // Giới hạn 10 phần tử đầu tiên
                    .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
            return ResponseEntity.ok(new APIRespone(true, "Success", map));

        } else {
            System.out.println("module: year");
            for (Product product : products) {
                Long q = orderDetailRepository.getCountProductSoldYear(currentYear, product.getProductId());
                if (q != null)
                    map.put(product.getProductName(), q);
            }
            // Sắp xếp map theo giá trị giảm dần và lấy 10 phần tử đầu tiên
            map = map.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // Sắp xếp theo giá trị giảm dần
                    .limit(10) // Giới hạn 10 phần tử đầu tiên
                    .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
            return ResponseEntity.ok(new APIRespone(true, "Success", map));
        }


    }

    @Override
    public ResponseEntity<APIRespone> getAllTotalAmountOrderStore(Long ownerId) {
        List<Store> stores = storeRepository.findAllByManagerId(ownerId);
        Long totalAmount = orderRepository.getTotalAmountForCompletedOrdersStore(stores.get(0).getStoreId());
        return ResponseEntity.ok(new APIRespone(true, "Success", totalAmount));
    }

    public ResponseEntity<APIRespone> getCountOrderByMonthStore(Long OwnerId) {
        List<Store> stores = storeRepository.findAllByManagerId(OwnerId);
        Long storeId = stores.get(0).getStoreId();
        LocalDateTime ldt = LocalDateTime.now();
        Long totalAmount = orderRepository.countOrdersByMonthStore(storeId, ldt.getMonthValue(), ldt.getYear());
        return ResponseEntity.ok(new APIRespone(true, "Success", totalAmount));

    }

    @Override
    public ResponseEntity<APIRespone> getTotalAmountByMonthStore(Long OwnerId, int year) {
        Map<String, Long> map = new LinkedHashMap<>();
        List<Store> stores = storeRepository.findAllByManagerId(OwnerId);
        Long storeId = stores.get(0).getStoreId();
        for (int i = 0; i < 12; i++) {
            // Lùi tháng
            int month = i + 1;
            Long totalAmount = orderRepository.getTotalAmountByMonthStore(storeId, month, year);
            map.put("Tháng " + month + " Năm " + year, totalAmount);
        }

        return ResponseEntity.ok(new APIRespone(true, "Success", map));
    }

    @Override
    public ResponseEntity<APIRespone> getTotalAmountByWeekStore(Long OwnerId) {
        List<Store> stores = storeRepository.findAllByManagerId(OwnerId);
        Long storeId = stores.get(0).getStoreId();
        LocalDateTime ldt = LocalDateTime.now();
        int currentMonth = ldt.getMonthValue();
        int currentYear = ldt.getYear();
        int currentDay = ldt.getDayOfMonth();
        Map<String, Long> map = new LinkedHashMap<>();

        for (int i = 0; i < 7; i++) {
            int day = currentDay - i;
            int month = currentMonth;
            int year = currentYear;
            if (day < 1) {
                day += 7;
                month -= 1;
                if (month < 1) {
                    month += 12;
                    year -= 1;
                }
            }
            Long totalAmount = orderRepository.getTotalAmountByWeekStore(storeId, day, month, year);
            map.put("Ngày " + day + " Tháng " + month + " Năm " + year, totalAmount);
        }
        return ResponseEntity.ok(new APIRespone(true, "Success", map));
    }

    @Override
    public ResponseEntity<APIRespone> getCountProductSoleStore(Long OwnerId, String module) {
        List<Store> stores = storeRepository.findAllByManagerId(OwnerId);
        Long storeId = stores.get(0).getStoreId();
        LocalDateTime ldt = LocalDateTime.now();
        int currentMonth = ldt.getMonthValue();
        int currentYear = ldt.getYear();
        int currentDay = ldt.getDayOfMonth();
        List<Product> products = productRepository.findAll();
        Map<String, Long> map = new LinkedHashMap<>();
        if (module.equals("day")) {
            System.out.println("module: day");
            for (Product product : products) {
                Long q = orderDetailRepository.getCountProductSoldDayStore(storeId, currentDay, currentMonth, currentYear, product.getProductId());
                if (q != null)
                    map.put(product.getProductName(), q);
            }
            // Sắp xếp map theo giá trị giảm dần và lấy 10 phần tử đầu tiên
            map = map.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // Sắp xếp theo giá trị giảm dần
                    .limit(10) // Giới hạn 10 phần tử đầu tiên
                    .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
            return ResponseEntity.ok(new APIRespone(true, "Success", map));

        } else if (module.equals("month")) {
            System.out.println("module: month");
            for (Product product : products) {
                Long q = orderDetailRepository.getCountProductSoldMonthStore(storeId, currentMonth, currentYear, product.getProductId());
                if (q != null)
                    map.put(product.getProductName(), q);
            }
            // Sắp xếp map theo giá trị giảm dần và lấy 10 phần tử đầu tiên
            map = map.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // Sắp xếp theo giá trị giảm dần
                    .limit(10) // Giới hạn 10 phần tử đầu tiên
                    .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
            return ResponseEntity.ok(new APIRespone(true, "Success", map));

        } else {
            System.out.println("module: year");
            for (Product product : products) {
                Long q = orderDetailRepository.getCountProductSoldYearStore(storeId, currentYear, product.getProductId());
                if (q != null)
                    map.put(product.getProductName(), q);
            }
            // Sắp xếp map theo giá trị giảm dần và lấy 10 phần tử đầu tiên
            map = map.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed()) // Sắp xếp theo giá trị giảm dần
                    .limit(10) // Giới hạn 10 phần tử đầu tiên
                    .collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), LinkedHashMap::putAll);
            return ResponseEntity.ok(new APIRespone(true, "Success", map));
        }
    }

    @Override
    public ResponseEntity<APIRespone> getOrderDetailOfStoreForOwner(Long ownerId, String orderCode) {
        List<Store> stores = storeRepository.findAllByManagerId(ownerId);
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        Optional<List<OrderDetail>> orderOptional = orderRepository.findOrderDetailsByOrderCodeAndStore(orderCode, stores.get(0).getStoreId());

        if (orderOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Order code not found", ""));
        }
        List<OrderDetail> orderDetails = orderOptional.get();
        OrderStore os = new OrderStore(orderDetails);
        return ResponseEntity.ok(new APIRespone(true, "Success", os));
    }

    @Override
    public ResponseEntity<APIRespone> getAllOrderDetailOfStoreForOwner(Long ownerId) {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            return ResponseEntity.ok(new APIRespone(true, "No order found", ""));
        }
        List<Store> stores = storeRepository.findAllByManagerId(ownerId);
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }

        // Kiểm tra xem có đơn hàng nào có chi tiết hợp lệ không
        List<Order> orders1 = orders.stream()
                .filter(order -> {
                    if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                        return order.getOrderDetails().stream()
                                .anyMatch(orderDetail -> orderDetail.getStore() != null && stores.contains(orderDetail.getStore()));
                    }
                    // Không in ra order nếu không có OrderDetail
                    return false;
                })
                .toList();

        if (orders1.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }
        Store store = stores.get(0);
        List<OrderStore> orderStores = new ArrayList<>();
        for (Order order : orders1) {
            if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                Optional<List<OrderDetail>> orderDetails = orderRepository.findOrderDetailsByOrderCodeAndStore(order.getOrderCode(), store.getStoreId());
                List<OrderDetail> orderDetailList = orderDetails.get();
                OrderStore os = new OrderStore(orderDetailList);
                orderStores.add(os);
            }
        }
        return ResponseEntity.ok(new APIRespone(true, "Success", orderStores));
    }
    @Override
    public ResponseEntity<APILazyOrders> getAllOrderByStatusOfStore1(String statusName, Long ownerId, int page, int size) {
        System.out.println(page);
        System.out.println((size));
        StatusOrder statusOrder = statusOrderRepository.findByStatusName(statusName);

        // Tìm các cửa hàng của chủ sở hữu
        List<Store> stores = storeRepository.findAllByManagerId(ownerId);
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APILazyOrders(false, 0, "Store not found", ""));
        }
        Store store = stores.get(0);
        // Cấu hình phân trang
        Pageable pageable = PageRequest.of(page, size);

        // Tìm các đơn hàng với phân trang
        Page<Order> pagedOrders = orderRepository.findOrdersWithStatusAndStores(store.getStoreId(), statusName, pageable);

        if (pagedOrders.isEmpty()) {
            return ResponseEntity.ok(new APILazyOrders(true, 0, "No order found", ""));
        }
        long totalElements = pagedOrders.getTotalElements(); // Tổng số bản ghi
        int totalPages = pagedOrders.getTotalPages(); // Tổng số trang
        List<OrderStore> orderStores = new ArrayList<>();
        for (Order order : pagedOrders.getContent()) {
            if (!order.getOrderDetails().isEmpty()) {
                Optional<List<OrderDetail>> orderDetails = orderRepository.findOrderDetailsByOrderCodeAndStore(order.getOrderCode(), stores.get(0).getStoreId());
                orderDetails.ifPresent(orderDetailList -> {
                    OrderStore os = new OrderStore(orderDetailList);
                    orderStores.add(os);
                });
            }
        }

//        orderStores.removeIf(os -> !os.getStatus().equals(statusName));

        return ResponseEntity.ok(new APILazyOrders(true, totalPages, "Success", orderStores));
    }

    @Override
    public ResponseEntity<APIRespone> getOrderDetails(Long ownerId,String order_code) {
        Optional<Order> order = orderRepository.findByOrderCode(order_code);
        if (order.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "No order found", ""));
        }
        Order or = order.get();
        List<Store> stores = storeRepository.findAllByManagerId(ownerId);
        if (stores.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        Store store = stores.get(0);

        List<OrderDetailResponse> orderDetailResponses =or.getOrderDetails().stream()
                .filter(orderDetail -> orderDetail.getStore() != null && orderDetail.getStore().getStoreId().equals(store.getStoreId()))
                .map(OrderDetailResponse::new)
                .collect(Collectors.toList());

        // Trả về kết quả
        return ResponseEntity.ok(new APIRespone(true, "Success", orderDetailResponses));
    }
}