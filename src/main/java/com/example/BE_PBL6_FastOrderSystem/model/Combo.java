package com.example.BE_PBL6_FastOrderSystem.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
public class Combo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comboId;
    private String comboName;
    private String image;
    private Double comboPrice;



    @ManyToMany //
    @JoinTable(
            name = "combo_product",
            joinColumns = @JoinColumn(name = "combo_id"), //
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products;
    // Set là một tập hợp không chứa các phần tử trùng lặp

    // Getters and setters

    public Long getComboId() {
        return comboId;
    }

    public void setComboId(Long comboId) {
        this.comboId = comboId;
    }

    public String getComboName() {
        return comboName;
    }

    public void setComboName(String comboName) {
        this.comboName = comboName;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public Double getComboPrice() {
        return comboPrice;
    }

    public void setComboPrice(Double comboPrice) {
        this.comboPrice = comboPrice;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}