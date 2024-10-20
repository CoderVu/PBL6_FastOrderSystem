package com.example.BE_PBL6_FastOrderSystem.controller.Onwer;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetails;
import com.example.BE_PBL6_FastOrderSystem.security.user.FoodUserDetailsService;
import com.example.BE_PBL6_FastOrderSystem.service.IOrderService;
import com.example.BE_PBL6_FastOrderSystem.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/owner/products")
@RequiredArgsConstructor
public class OwnerProductController {
    @Autowired
    private final IProductService productService;
    private final IOrderService orderService;
    @Autowired
    private FoodUserDetailsService foodUserDetailsService;
    private Long getCurrentUserId() {
        return FoodUserDetails.getCurrentUserId();
    }
    @GetMapping("/get-all-products")
    public ResponseEntity<APIRespone> getAllProducts(@RequestParam Long storeId) {
        return productService.getProductsByStoreId(storeId);
    }

    @PostMapping("/remove-list-products-from-store")
    public ResponseEntity<APIRespone> removeListProductsFromStore(
            @RequestParam("storeId") Long storeId,
            @RequestParam("productIds") List<Long> productIds) {
        return productService.removeProductsFromStore(productIds, storeId);
    }
    @PostMapping("/add-to-store")
    public ResponseEntity<APIRespone> addProductToStore(
        @RequestParam("storeId") Long storeId,
        @RequestParam("productIds") List<Long> productIds,
        @RequestParam("quantity") List<Integer> quantity) {
        Long managerId = getCurrentUserId();
            return productService.applyProductsToStoreOfOwner(managerId, storeId, productIds, quantity);
    }
    @PutMapping("/update-quantity")
    public ResponseEntity<APIRespone> updateQuantityProductOfOwner(
            @RequestParam("storeId") Long storeId,
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
        Long managerId = getCurrentUserId();
        return productService.updateQuantityProductOfOwner(managerId, storeId, productId, quantity);
    }


}