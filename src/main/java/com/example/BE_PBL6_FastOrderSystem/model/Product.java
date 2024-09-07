package com.example.BE_PBL6_FastOrderSystem.model;

import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;
    private String productName;
    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image;
    private String description;
    private Double price;
    private Double discountedPrice;
    @ManyToMany(mappedBy = "products") // để tránh việc tạo bảng trung gian
    private Set<Promotion> promotions = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @OneToMany(mappedBy = "product")
    private Set<ProductStore> productStores = new HashSet<>();

    // Getters and Setters for productStores
    public Set<ProductStore> getProductStores() {
        return productStores;
    }

    public void setProductStores(Set<ProductStore> productStores) {
        this.productStores = productStores;
    }
    private Integer stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean bestSale;
    @ManyToMany(mappedBy = "products")
    private Set<Combo> combos = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getImage() {
        return image;
    }
   public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    public Double getDiscountedPrice() {
        return discountedPrice;
    }
    public void setDiscountedPrice(Double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
    public Set<Promotion> getPromotions() {
        return promotions;
    }
    public void setPromotions(Set<Promotion> promotions) {
        this.promotions = promotions;
    }


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    public Set<Promotion> getPromotion() {
        return promotions;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    public Boolean getBestSale() {
        return bestSale;
    }

    public void setBestSale(Boolean bestSale) {
        this.bestSale = bestSale;
    }
    public Set<Combo> getCombos() {
        return combos;
    }

    public void setCombos(Set<Combo> combos) {
        this.combos = combos;
    }
}