package com.example.BE_PBL6_FastOrderSystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class ShipperOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "shipper_id", nullable = false)
    private User shipper;
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
    @OneToMany
    @JoinColumn(name = "order_detail_id")
    private List<OrderDetail> orderDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String deliveryStatus;
}
