package com.example.BE_PBL6_FastOrderSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long orderDetailId;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne
    @JoinColumn(name = "combo_id")
    private Combo combo;
    @ManyToMany
    @JoinTable(
            name = "order_detail_drink_products",
            joinColumns = @JoinColumn(name = "order_detail_id"),
            inverseJoinColumns = @JoinColumn(name = "drink_product_id")
    )
    private List<Product> drinkProducts;

    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private StatusOrder status;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipper_order_id")
    private ShipperOrder shipperOrder;

}