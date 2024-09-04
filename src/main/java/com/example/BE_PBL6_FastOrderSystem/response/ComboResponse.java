package com.example.BE_PBL6_FastOrderSystem.response;

import com.example.BE_PBL6_FastOrderSystem.model.Combo;

public class ComboResponse {
    private Long comboId;
    private String comboName;
    private double price;
    private String image;
    private String description;
    public ComboResponse(Long comboId, String comboName, double price, String image, String description) {
        this.comboId = comboId;
        this.comboName = comboName;
        this.price = price;
        this.image = image;
        this.description = description;

    }

    public ComboResponse(Combo combo) {
        this.comboId = combo.getComboId();
        this.comboName = combo.getComboName();
        this.price = combo.getComboPrice();
        this.image = combo.getImage();
    }

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
