package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);

// interface là nơi chứa các phương thức truy vấn dữ liệu từ cơ sở dữ liệu
    List<CartItem> findByCartId(Long cartId);


    List<CartItem> findByCombo_ComboId(Long comboId);
}