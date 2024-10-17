package com.example.BE_PBL6_FastOrderSystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
    private List<Product> products = new ArrayList<>();
    @OneToMany(mappedBy = "combo", cascade = CascadeType.ALL)
    private List<Rate> rates = new ArrayList<>();
}