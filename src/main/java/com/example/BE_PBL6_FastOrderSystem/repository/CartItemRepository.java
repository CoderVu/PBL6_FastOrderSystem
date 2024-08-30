package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);

// interface là nơi chứa các phương thức truy vấn dữ liệu từ cơ sở dữ liệu
    List<Cart> findByCartId(Long cartId);


    List<Cart> findByCombo_ComboId(Long comboId);
}