package com.example.BE_PBL6_FastOrderSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@EqualsAndHashCode(exclude = {"manager", "promotions", "productStores"})
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;
    private String storeName;
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;
    private String phoneNumber;
    private String location;
    private Double longitude;
    private Double latitude;
    private LocalDateTime openingTime;
    private LocalDateTime closingTime;
    @OneToOne
    @JoinColumn(name = "manager_id")
    private User manager;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToMany(mappedBy = "stores")
    private List<Promotion> promotions = new ArrayList<>();
    @OneToMany(mappedBy = "store")
    private List<ProductStore> productStores = new ArrayList<>();
    @Override
    public String toString() {
        return "Store{" +
                "storeId=" + storeId +
                ", storeName='" + storeName + '\'' +
                ", image='" + image + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", location='" + location + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", openingTime=" + openingTime +
                ", closingTime=" + closingTime +
                ", manager=" + manager +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
