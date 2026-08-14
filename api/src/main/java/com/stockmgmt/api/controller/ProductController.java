package com.stockmgmt.api.controller;

import com.stockmgmt.api.entity.dto.request.CreateProductRequest;
import com.stockmgmt.api.entity.dto.response.*;
import com.stockmgmt.api.service.ProductService;
import com.stockmgmt.api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @PostMapping("/stores/{storeId}/categories")
    public ResponseEntity<CategoryResponse> createCategory(@PathVariable UUID storeId,
                                                           @RequestParam String name,
                                                           @RequestParam(required = false) String description) {
        return ResponseEntity.ok(categoryService.createCategory(storeId, name, description));
    }

    @GetMapping("/stores/{storeId}/categories")
    public ResponseEntity<List<CategoryResponse>> getCategories(@PathVariable UUID storeId) {
        return ResponseEntity.ok(categoryService.getCategories(storeId));
    }

    @PostMapping("/stores/{storeId}/products")
    public ResponseEntity<ProductResponse> createProduct(@PathVariable UUID storeId,
                                                          @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(storeId, request));
    }

    @GetMapping("/stores/{storeId}/products")
    public ResponseEntity<List<ProductResponse>> getProducts(@PathVariable UUID storeId) {
        return ResponseEntity.ok(productService.getProducts(storeId));
    }

    @GetMapping("/stores/{storeId}/products/active")
    public ResponseEntity<List<ProductResponse>> getActiveProducts(@PathVariable UUID storeId) {
        return ResponseEntity.ok(productService.getActiveProducts(storeId));
    }

    @GetMapping("/stores/{storeId}/products/archived")
    public ResponseEntity<List<ProductResponse>> getArchivedProducts(@PathVariable UUID storeId) {
        return ResponseEntity.ok(productService.getArchivedProducts(storeId));
    }

    @GetMapping("/stores/{storeId}/products/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID storeId, @PathVariable UUID productId) {
        return ResponseEntity.ok(productService.getProduct(storeId, productId));
    }

    @PutMapping("/stores/{storeId}/products/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID storeId,
                                                          @PathVariable UUID productId,
                                                          @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(storeId, productId, request));
    }

    @DeleteMapping("/stores/{storeId}/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID storeId, @PathVariable UUID productId) {
        productService.deleteProduct(storeId, productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/stores/{storeId}/products/{productId}/archive")
    public ResponseEntity<ProductResponse> archiveProduct(@PathVariable UUID storeId, @PathVariable UUID productId) {
        return ResponseEntity.ok(productService.archiveProduct(storeId, productId));
    }

    @PutMapping("/stores/{storeId}/products/{productId}/activate")
    public ResponseEntity<ProductResponse> activateProduct(@PathVariable UUID storeId, @PathVariable UUID productId) {
        return ResponseEntity.ok(productService.activateProduct(storeId, productId));
    }

    @GetMapping("/stores/{storeId}/products/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@PathVariable UUID storeId,
                                                                @RequestParam(required = false) String q) {
        if (q == null || q.isBlank()) {
            return ResponseEntity.ok(productService.getProducts(storeId));
        }
        return ResponseEntity.ok(productService.searchProducts(storeId, q));
    }

    @GetMapping("/stores/{storeId}/products/filter")
    public ResponseEntity<List<ProductResponse>> filterProducts(@PathVariable UUID storeId,
                                                                @RequestParam(required = false) UUID categoryId,
                                                                @RequestParam(required = false) Boolean active,
                                                                @RequestParam(required = false) Boolean archived) {
        return ResponseEntity.ok(productService.filterProducts(storeId, categoryId, active, archived));
    }
}
