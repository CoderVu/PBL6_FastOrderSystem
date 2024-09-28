package com.example.BE_PBL6_FastOrderSystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;
@Data
@Entity
public class Combo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comboId;
    private String comboName;
    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;
    private Double comboPrice;
    private String description;
    @ManyToMany
    @JoinTable(
            name = "combo_product",
            joinColumns = @JoinColumn(name = "combo_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products;
}