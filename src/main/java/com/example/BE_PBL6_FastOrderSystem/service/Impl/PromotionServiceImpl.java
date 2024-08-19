package com.example.BE_PBL6_FastOrderSystem.service.Impl;

import com.example.BE_PBL6_FastOrderSystem.exception.AlreadyExistsException;
import com.example.BE_PBL6_FastOrderSystem.exception.ResourceNotFoundException;
import com.example.BE_PBL6_FastOrderSystem.model.Product;
import com.example.BE_PBL6_FastOrderSystem.model.Promotion;
import com.example.BE_PBL6_FastOrderSystem.model.Store;
import com.example.BE_PBL6_FastOrderSystem.repository.ProductRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.PromotionRepository;
import com.example.BE_PBL6_FastOrderSystem.repository.StoreRepository;
import com.example.BE_PBL6_FastOrderSystem.request.PromotionRequest;
import com.example.BE_PBL6_FastOrderSystem.response.PromotionResponse;
import com.example.BE_PBL6_FastOrderSystem.service.IPromotionService;
import lombok.RequiredArgsConstructor;
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

    @Override
    public List<PromotionResponse> getAllPromotion() {
        return promotionRepository.findAll().stream()
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
    }

    @Override
    public Optional<PromotionResponse> getPromotionById(Long promotionId) {
        return promotionRepository.findById(promotionId)
                .map(promotion -> new PromotionResponse(
                        promotion.getId(),
                        promotion.getName(),
                        promotion.getDescription(),
                        promotion.getDiscountPercentage(),
                        promotion.getStartDate(),
                        promotion.getEndDate(),
                        promotion.getStores().stream().map(store -> store.getStoreId()).collect(Collectors.toList())
                ));
    }

    @Override
    public PromotionResponse addPromotion(PromotionRequest promotionRequest) {
        if (promotionRepository.existsByName(promotionRequest.getName())) {
            throw new AlreadyExistsException("Promotion name already exists");
        }
        Promotion promotion = new Promotion();
        promotion.setName(promotionRequest.getName());
        promotion.setDescription(promotionRequest.getDescription());
        promotion.setDiscountPercentage(promotionRequest.getDiscountPercentage());
        promotion.setStartDate(promotionRequest.getStartDate());
        promotion.setEndDate(promotionRequest.getEndDate());
        promotionRepository.save(promotion);
        return new PromotionResponse(
                promotion.getId(),
                promotion.getName(),
                promotion.getDescription(),
                promotion.getDiscountPercentage(),
                promotion.getStartDate(),
                promotion.getEndDate()
        );
    }

    @Override
    public PromotionResponse applyPromotionToStore(Long promotionId, Long storeId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));
        promotion.getStores().add(store);
        promotionRepository.save(promotion);
        return new PromotionResponse(
                promotion.getId(),
                promotion.getName(),
                promotion.getDescription(),
                promotion.getDiscountPercentage(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.getStores().stream().map(Store::getStoreId).collect(Collectors.toList())
        );
    }

    @Override
    public PromotionResponse applyPromotionToAllStores(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
        // lấy ra tất cả id của các store
        List<Long> storeIds = storeRepository.findAll().stream()
                .map(Store::getStoreId)
                .collect(Collectors.toList());
        // lưu promotion vào tất cả các store bằng storeId
        storeIds.forEach(id -> applyPromotionToStore(promotionId, id));
        promotionRepository.save(promotion);
        return new PromotionResponse(
                promotion.getId(),
                promotion.getName(),
                promotion.getDescription(),
                promotion.getDiscountPercentage(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                storeIds
        );
    }
    public PromotionResponse applyPromotionToProduct(Long promotionId, Long productId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id " + promotionId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));

        boolean isPromotionInProductStores = product.getStores().stream()
                .anyMatch(store -> promotion.getStores().contains(store));

        if (isPromotionInProductStores) {
            // Thêm khuyến mãi vào sản phẩm
            product.getPromotions().add(promotion);

            // Thêm sản phẩm vào khuyến mãi
            promotion.getProducts().add(product);

            // Lưu sản phẩm và khuyến mãi
            productRepository.save(product);
            promotionRepository.save(promotion);
        } else {
            throw new ResourceNotFoundException("Promotion does not belong to the product's store");
        }

        return new PromotionResponse(
                promotion.getId(),
                promotion.getName(),
                promotion.getDescription(),
                promotion.getDiscountPercentage(),
                promotion.getStartDate(),
                promotion.getEndDate(),
                promotion.getStores().stream().map(Store::getStoreId).collect(Collectors.toList())
        );
    }




}