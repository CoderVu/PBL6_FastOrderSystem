package com.example.BE_PBL6_FastOrderSystem.request;

import lombok.Data;

@Data
public class CartRequest {
    private Long productId;
    private int quantity;
    private Long storeId;
    private String status;
    private String deliveryAddress;
    public CartRequest(Long productId, int quantity, Long storeId, String status, String deliveryAddress) {
        this.productId = productId;
        this.quantity = quantity;
        this.storeId = storeId;
        this.status = status;
        this.deliveryAddress = deliveryAddress;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
