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
    @OneToMany(mappedBy = "shipperOrder", fetch = FetchType.EAGER)
    private List<OrderDetail> orderDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status;
    private LocalDateTime lastAssignedAt;
    private LocalDateTime receivedAt;
    private LocalDateTime deliveredAt;
    private String note;
}

