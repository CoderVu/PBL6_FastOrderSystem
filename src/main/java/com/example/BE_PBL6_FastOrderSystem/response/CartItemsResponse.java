package com.example.BE_PBL6_FastOrderSystem.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemsResponse {
    private Long cartItemId; //
    private Long userId;//
    private Long productId;//
    private String productName;
    private String image;
    private int quantity; //
    private double unitPrice; //
    private double totalPrice; //
    private Long storeId; //
    private String status; //
    private LocalDateTime createdAt;//
    private LocalDateTime updatedAt;//
}