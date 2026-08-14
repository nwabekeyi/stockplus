package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.request.CreateProductRequest;
import com.stockmgmt.api.entity.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    ProductResponse createProduct(UUID storeId, CreateProductRequest request);
    List<ProductResponse> getProducts(UUID storeId);
    List<ProductResponse> getActiveProducts(UUID storeId);
    List<ProductResponse> getArchivedProducts(UUID storeId);
    ProductResponse getProduct(UUID storeId, UUID productId);
    ProductResponse updateProduct(UUID storeId, UUID productId, CreateProductRequest request);
    void deleteProduct(UUID storeId, UUID productId);
    ProductResponse archiveProduct(UUID storeId, UUID productId);
    ProductResponse activateProduct(UUID storeId, UUID productId);
    List<ProductResponse> searchProducts(UUID storeId, String query);
    List<ProductResponse> filterProducts(UUID storeId, UUID categoryId, Boolean active, Boolean archived);
}
