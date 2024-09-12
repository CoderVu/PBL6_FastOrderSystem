package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.model.Combo;
import com.example.BE_PBL6_FastOrderSystem.model.Product;
import com.example.BE_PBL6_FastOrderSystem.model.Store;

import java.util.List;
import java.util.stream.Collectors;

public class ComboResponse {
    private Long comboId;
    private String comboName;
    private double price;
    private String image;
    private String description;
//    private List<ProductResponse> products;
//    private StoreResponse store;

    public ComboResponse(Long comboId, String comboName, double price, String image, String description) {
        this.comboId = comboId;
        this.comboName = comboName;
        this.price = price;
        this.image = image;
        this.description = description;
//        this.products = products;
//        this.store = store;
    }
    public ComboResponse(Combo combo) {
        this.comboId = combo.getComboId();
        this.comboName = combo.getComboName();
        this.price = combo.getComboPrice();
        this.image = combo.getImage();
    }

    // Getters and setters for all fields

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

}