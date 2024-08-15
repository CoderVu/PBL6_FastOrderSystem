package com.example.BE_PBL6_FastOrderSystem.response;

public class ComboResponse {
    private Long comboId;
    private String comboName;
    private double price;
    private String image;
    public ComboResponse(Long comboId, String comboName, double price, String image) {
        this.comboId = comboId;
        this.comboName = comboName;
        this.price = price;
        this.image = image;
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
}
