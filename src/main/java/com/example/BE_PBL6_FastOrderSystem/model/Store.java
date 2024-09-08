package com.example.BE_PBL6_FastOrderSystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;
    private String storeName;
    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;
    private String phoneNumber;
    private String location;
    private Double longitude;
    private Double latitude;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToMany(mappedBy = "stores")
    private Set<Promotion> promotions;
    @OneToMany(mappedBy = "store")
    private Set<ProductStore> productStores = new HashSet<>();
}
