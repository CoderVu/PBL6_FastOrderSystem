package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.model.Product;
import com.example.BE_PBL6_FastOrderSystem.model.Promotion;
import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.repository.ProductRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.PromotionRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.StoreRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.UserRepository;
import com.example.BE_PBL6_FastOrderSystem.request.PromotionRequest;
import com.example.BE_PBL6_FastOrderSystem.response.APIRespone;
import com.example.BE_PBL6_FastOrderSystem.response.PromotionResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IPromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements IPromotionService {
    private final PromotionRepository promotionRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<APIRespone> getAllPromotion() {
    if (promotionRepository.findAll().isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "No promotion found", ""), HttpStatus.NOT_FOUND);
        }
        List<PromotionResponse> promotionResponses = promotionRepository.findAll().stream()
                .map(promotion -> new PromotionResponse(
                        promotion.getId(),
                        promotion.getName(),
                        promotion.getDescription(),
                        promotion.getDiscountPercentage(),
                        promotion.getStartDate(),
                        promotion.getEndDate(),
                        promotion.getStores().stream().map(store -> store.getStoreId()).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
        return new ResponseEntity<>(new APIRespone(true, "Success", promotionResponses), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<APIRespone> getPromotionById(Long promotionId) {
       Optional<Promotion> promotion = promotionRepository.findById(promotionId);
        if (promotion.isEmpty()) {
            return new ResponseEntity<>(new APIRespone(false, "Promotion not found", ""), HttpStatus.NOT_FOUND);
        }
        PromotionResponse promotionResponse = new PromotionResponse(
                promotion.get().getId(),
                promotion.get().getName(),
                promotion.get().getDescription(),
                promotion.get().getDiscountPercentage(),
                promotion.get().getStartDate(),
                promotion.get().getEndDate(),
                promotion.get().getStores().stream().map(store -> store.getStoreId()).collect(Collectors.toList())
        );
        return new ResponseEntity<>(new APIRespone(true, "Success", promotionResponse), HttpStatus.OK);


    }

    @Override
    public ResponseEntity<APIRespone> addPromotion(PromotionRequest promotionRequest) {
        if (promotionRepository.existsByName(promotionRequest.getName())) {
            return new ResponseEntity<>(new APIRespone(false, "Promotion already exists", ""), HttpStatus.BAD_REQUEST);
        }
        Promotion promotion = new Promotion();
        promotion.setName(promotionRequest.getName());
        promotion.setDescription(promotionRequest.getDescription());
        promotion.setDiscountPercentage(promotionRequest.getDiscountPercentage());
        promotion.setStartDate(promotionRequest.getStartDate());
        promotion.setEndDate(promotionRequest.getEndDate());
        promotionRepository.save(promotion);
     return new ResponseEntity<>(new APIRespone(true, "Promotion added successfully", promotion), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<APIRespone> applyPromotionToStore(Long promotionId, Long storeId) {
        Optional<Promotion> promotionOptional = promotionRepository.findById(promotionId);
        if (promotionOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Promotion not found", ""));
        }
        Promotion promotion = promotionOptional.get();
        Optional<Store> storeOptional = storeRepository.findById(storeId);
        if (storeOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Store not found", ""));
        }
        Store store = storeOptional.get();
        promotion.getStores().add(store);
        promotionRepository.save(promotion);
        PromotionResponse response = new PromotionResponse(
                promotion.getId(),
                promotion.getName(),
                promotion.getDescription(),
                promotion.getDiscountPercentage(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.getStores().stream()
                        .map(Store::getStoreId)
                        .collect(Collectors.toList())
        );

        return ResponseEntity.ok(new APIRespone(true, "Promotion applied to store successfully", response));
    }


    @Override
    public ResponseEntity<APIRespone> applyPromotionToAllStores(Long promotionId) {
        Optional<Promotion> promotionOptional = promotionRepository.findById(promotionId);
        if (promotionOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Promotion not found", ""));
        }
        Promotion promotion = promotionOptional.get();
        List<Long> storeIds = storeRepository.findAll().stream()
                .map(Store::getStoreId)
                .collect(Collectors.toList());
        storeIds.forEach(id -> applyPromotionToStore(promotionId, id));
        promotionRepository.save(promotion);
        return ResponseEntity.ok(new APIRespone(true, "Promotion applied to all stores successfully", new PromotionResponse(
                promotion.getId(),
                promotion.getName(),
                promotion.getDescription(),
                promotion.getDiscountPercentage(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                storeIds
        )));
    }

    @Override
    public ResponseEntity<APIRespone> applyPromotionToProduct( Long promotionId, Long productId) {
        Optional<Promotion> promotionOptional = promotionRepository.findById(promotionId);
        if (promotionOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Promotion not found with id " + promotionId, ""));
        }
        Promotion promotion = promotionOptional.get();
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Product not found with id " + productId, ""));
        }
        Product product = productOptional.get();
        boolean isPromotionInProductStores = product.getStores().stream()
                .anyMatch(store -> promotion.getStores().contains(store));
        if (isPromotionInProductStores) {
            product.getPromotions().add(promotion);
            promotion.getProducts().add(product);
            productRepository.save(product);
            promotionRepository.save(promotion);
            return ResponseEntity.ok(new APIRespone(true, "Promotion applied to product successfully", new PromotionResponse(
                    promotion.getId(),
                    promotion.getName(),
                    promotion.getDescription(),
                    promotion.getDiscountPercentage(),
                    promotion.getStartDate(),
                    promotion.getEndDate(),
                    promotion.getStores().stream().map(Store::getStoreId).collect(Collectors.toList())
            )));
        } else {
            return ResponseEntity.badRequest().body(new APIRespone(false, "Promotion does not belong to the product's store", ""));
        }
    }





}