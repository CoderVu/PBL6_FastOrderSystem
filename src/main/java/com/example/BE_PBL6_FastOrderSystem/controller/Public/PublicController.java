package com.example.BE_PBL6_FastOrderSystem.controller.Public;

import com.example.BE_PBL6_FastOrderSystem.response.*;
import com.example.BE_PBL6_FastOrderSystem.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/public")
public class PublicController {
    private final IProductService productService;
    private final IComboService comboService;
    private final ICategoryService categoryService;
    private final IPromotionService promotionService;
    private final IStoreService storeService;
    private final ISizeService sizeService;
    private final IStatusOrderService statusOrderService;
    private final IStaffService scheduleService;

    private final IOrderService orderService;
    @GetMapping("/categories/all")
    public  ResponseEntity<APIRespone> getAllCategories() {
     return categoryService.getAllCategories();
    }
    @GetMapping("/categories/{id}")
    public ResponseEntity<APIRespone>  getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }
    @GetMapping("/products/all")
    public ResponseEntity<APIRespone> getAllProducts() throws SQLException {
       return productService.getAllProduct();
    }
    @GetMapping("/products/{storeid}/{categoryid}")
    public ResponseEntity<APIRespone> getProduct_ByStoreAndCategoryById(@PathVariable("storeid") Long storeId,@PathVariable("categoryid") Long categoryid) {
        return productService.getProductsByStore_CategoryId(storeId,categoryid);
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<APIRespone> getProductById(@PathVariable("id") Long productId) {
        return productService.getProductById(productId);
    }
    @GetMapping("/products/store/{storeId}")
    public ResponseEntity<APIRespone> getProductsByStoreId(@PathVariable("storeId") Long storeId) {
       return productService.getProductsByStoreId(storeId);
    }
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<APIRespone> getProductsByCategoryId(@PathVariable("categoryId") Long categoryId) {
       return productService.getProductsByCategoryId(categoryId);
    }
    @GetMapping("/products/search")
    public ResponseEntity<APIRespone> getProductByNames(@RequestParam("name") String productName) {
     return productService.getProductByNames(productName);
    }
    @GetMapping("/products/best-sale")
    public ResponseEntity<APIRespone> getBestSaleProducts() {
       return productService.getBestSaleProduct();
    }
    @GetMapping("/products/combos/{id}")
    public ResponseEntity<APIRespone> getProductsByComboId(@PathVariable("id") Long comboId) {
       return comboService.getProductsByComboId(comboId);
    }
    @GetMapping("/combo/all")
    public ResponseEntity<APIRespone> getCombos() {
       return comboService.getAllCombos();
    }

    @GetMapping("/combo/{id}")
      public ResponseEntity<APIRespone> getComboById(@PathVariable("id") Long comboId) {
         return comboService.getComboById(comboId);
      }
    @GetMapping("/combo/store/{storeId}")
      public ResponseEntity<APIRespone> getCombosByStoreId(@PathVariable("storeId") Long storeId) {
         return comboService.getCombosByStoreId(storeId);
      }
    @GetMapping("/stores/all")
    public ResponseEntity<APIRespone> getStores() {
         return storeService.getAllStores();
    }
    @GetMapping("/stores/{id}")
    public ResponseEntity<APIRespone> getStoreById(@PathVariable("id") Long storeId) {
        return storeService.getStoreById(storeId);
    }
    @GetMapping("/category/stores/{id}")
    public ResponseEntity<APIRespone> getCategoryByStoreId(@PathVariable("id") Long storeId) {
        return categoryService.getCategoryByStoreId(storeId);
    }

    @GetMapping("/promotions/all")
    public ResponseEntity<APIRespone> getPromotions() {
       return promotionService.getAllPromotion();

    }
    @GetMapping("/promotions/{id}")
    public ResponseEntity<APIRespone> getPromotionById(@PathVariable("id") Long promotionId) {
         return promotionService.getPromotionById(promotionId);
    }
    @GetMapping("/sizes/all")
      public ResponseEntity<APIRespone> getSizes() {
         return sizeService.getAllSizes();
      }
    @GetMapping("/sizes/{id}")
      public ResponseEntity<APIRespone> getSizeById(@PathVariable("id") Long sizeId) {
         return sizeService.getSizeById(sizeId);
      }
    @GetMapping("/status/all")
    public ResponseEntity<APIRespone> getStatus() {
        return statusOrderService.getAllStatus();
    }
    @PostMapping("/search/shipper")
    public ResponseEntity<APIRespone> searchShipper(@RequestParam Double longitude, @RequestParam Double latitude, @RequestParam int limit) {
        return orderService.findNearestShipper(longitude, latitude, limit);
    }
}
