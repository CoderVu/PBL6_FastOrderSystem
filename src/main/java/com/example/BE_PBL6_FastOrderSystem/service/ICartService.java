package com.example.BE_PBL6_FastOrderSystem.service;

import com.example.BE_PBL6_FastOrderSystem.request.CartRequest;

public interface ICartService {
    void addToCart(Long userId, CartRequest cartRequest);
}
