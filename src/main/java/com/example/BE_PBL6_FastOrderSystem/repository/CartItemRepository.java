package com.example.BE_PBL6_FastOrderSystem.repository;

import com.example.BE_PBL6_FastOrderSystem.entity.Cart;
import com.example.BE_PBL6_FastOrderSystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUserId(Long userId);

    List<Cart> findByCartId(Long cartId);


    List<Cart> findByProductIn(List<Product> products);


    @Query("SELECT c FROM Cart c WHERE c.user.id = ?1 AND c.product.productId = ?2 AND c.size.name= ?3 AND c.storeId = ?4")
    Cart findByUserIdAndProductIdAndSizeAndStoreId(Long userId, Long productId, String size, Long storeId);
    @Query("SELECT c FROM Cart c WHERE c.user.id = ?1 AND c.combo.comboId = ?2 AND c.size.name= ?3 AND c.storeId = ?4")
    Cart findByUserIdAndComboIdAndSizeAndStoreId(Long userId, Long comboId, String size, Long storeId);
    @Query("SELECT c FROM Cart c WHERE c.user.id = ?1 AND c.cartId = ?2")
    Cart findByUserIdAndCartId(Long userId, Long cartId);
}