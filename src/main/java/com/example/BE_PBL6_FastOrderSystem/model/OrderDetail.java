package com.example.BE_PBL6_FastOrderSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @ManyToOne
    @JoinColumn(name = "drink_product_id")
    private Product drinkProduct;

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


}