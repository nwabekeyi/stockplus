package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.*;
import com.stockmgmt.api.entity.dto.request.CreateProductRequest;
import com.stockmgmt.api.entity.dto.response.*;
import com.stockmgmt.api.entity.enumeration.UnitOfMeasure;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.*;
import com.stockmgmt.api.service.CloudinaryService;
import com.stockmgmt.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private static final String SKU_PREFIX = "SKU-";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StockRepository stockRepository;
    private final StoreRepository storeRepository;
    private final SupplierRepository supplierRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductLocationRepository productLocationRepository;
    private final WholesalePriceRuleRepository wholesalePriceRuleRepository;
    private final CloudinaryService cloudinaryService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public ProductResponse createProduct(UUID storeId, CreateProductRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        String sku = request.getSku();
        if (sku == null || sku.isBlank()) {
            sku = generateSku(storeId);
        }
        if (productRepository.existsByStore_IdAndSku(storeId, sku)) {
            throw new RuntimeException("Product SKU already exists");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sellingPrice(request.getSellingPrice())
                .costPrice(request.getCostPrice())
                .wholesalePrice(request.getWholesalePrice() != null ? request.getWholesalePrice() : BigDecimal.ZERO)
                .sku(sku)
                .barcode(request.getBarcode())
                .image(request.getImage())
                .active(request.getActive() != null ? request.getActive() : true)
                .archived(false)
                .minStockLevel(request.getMinStockLevel() != null ? request.getMinStockLevel() : 0)
                .maxStockLevel(request.getMaxStockLevel())
                .batchNumber(request.getBatchNumber())
                .expiryDate(request.getExpiryDate())
                .category(category)
                .store(store)
                .supplier(supplier)
                .build();

        productRepository.save(product);

        Stock stock = Stock.builder()
                .product(product)
                .quantity(request.getInitialQuantity() != null ? request.getInitialQuantity() : 0)
                .lowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : 10)
                .unit(request.getUnit() != null ? request.getUnit() : UnitOfMeasure.PIECE)
                .trackInventory(request.getTrackInventory() != null ? request.getTrackInventory() : true)
                .batchNumber(request.getBatchNumber())
                .expiryDate(request.getExpiryDate())
                .minStockLevel(request.getMinStockLevel() != null ? request.getMinStockLevel() : 0)
                .maxStockLevel(request.getMaxStockLevel())
                .build();
        stockRepository.save(stock);
        product.setStock(stock);

        if (request.getWholesaleRules() != null && !request.getWholesaleRules().isEmpty()) {
            List<WholesalePriceRule> rules = request.getWholesaleRules().stream()
                    .map(r -> WholesalePriceRule.builder()
                            .product(product)
                            .minQuantity(r.getMinQuantity())
                            .maxQuantity(r.getMaxQuantity())
                            .price(r.getPrice())
                            .build())
                    .toList();
            wholesalePriceRuleRepository.saveAll(rules);
            product.setWholesaleRules(rules);
        }

        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getProducts(UUID storeId) {
        return productRepository.findByStore_Id(storeId).stream()
                .filter(p -> !p.isArchived())
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getActiveProducts(UUID storeId) {
        return productRepository.findByStore_IdAndActiveAndArchived(storeId, true, false).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> getArchivedProducts(UUID storeId) {
        return productRepository.findByStore_IdAndArchived(storeId, true).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductResponse getProduct(UUID storeId, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Product does not belong to this store");
        }
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(UUID storeId, UUID productId, CreateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Product does not belong to this store");
        }

        String oldImage = product.getImage();
        if (request.getImage() != null && !request.getImage().equals(oldImage)) {
            cloudinaryService.deleteImage(oldImage);
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSellingPrice(request.getSellingPrice());
        product.setCostPrice(request.getCostPrice());
        product.setWholesalePrice(request.getWholesalePrice() != null ? request.getWholesalePrice() : product.getWholesalePrice());
        product.setBarcode(request.getBarcode());
        product.setImage(request.getImage());
        product.setActive(request.getActive() != null ? request.getActive() : product.isActive());
        product.setMinStockLevel(request.getMinStockLevel() != null ? request.getMinStockLevel() : product.getMinStockLevel());
        product.setMaxStockLevel(request.getMaxStockLevel() != null ? request.getMaxStockLevel() : product.getMaxStockLevel());
        product.setBatchNumber(request.getBatchNumber());
        product.setExpiryDate(request.getExpiryDate());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }

        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
            product.setSupplier(supplier);
        }

        if (product.getStock() != null) {
            product.getStock().setQuantity(request.getInitialQuantity() != null ? request.getInitialQuantity() : product.getStock().getQuantity());
            product.getStock().setLowStockThreshold(request.getLowStockThreshold() != null ? request.getLowStockThreshold() : product.getStock().getLowStockThreshold());
            product.getStock().setUnit(request.getUnit() != null ? request.getUnit() : product.getStock().getUnit());
            product.getStock().setTrackInventory(request.getTrackInventory() != null ? request.getTrackInventory() : product.getStock().isTrackInventory());
            product.getStock().setBatchNumber(request.getBatchNumber());
            product.getStock().setExpiryDate(request.getExpiryDate());
            product.getStock().setMinStockLevel(request.getMinStockLevel() != null ? request.getMinStockLevel() : product.getStock().getMinStockLevel());
            product.getStock().setMaxStockLevel(request.getMaxStockLevel() != null ? request.getMaxStockLevel() : product.getStock().getMaxStockLevel());
        }

        if (request.getWholesaleRules() != null) {
            wholesalePriceRuleRepository.deleteByProduct_Id(product.getId());
            List<WholesalePriceRule> rules = request.getWholesaleRules().stream()
                    .map(r -> WholesalePriceRule.builder()
                            .product(product)
                            .minQuantity(r.getMinQuantity())
                            .maxQuantity(r.getMaxQuantity())
                            .price(r.getPrice())
                            .build())
                    .toList();
            wholesalePriceRuleRepository.saveAll(rules);
            product.setWholesaleRules(rules);
        }

        productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID storeId, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Product does not belong to this store");
        }

        cloudinaryService.deleteImage(product.getImage());

        List<ProductImage> productImages = productImageRepository.findByProduct_IdOrderBySortOrderAsc(productId);
        for (ProductImage productImage : productImages) {
            cloudinaryService.deleteImage(productImage.getUrl());
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductResponse archiveProduct(UUID storeId, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Product does not belong to this store");
        }
        product.setArchived(true);
        product.setActive(false);
        productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse activateProduct(UUID storeId, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (!product.getStore().getId().equals(storeId)) {
            throw new RuntimeException("Product does not belong to this store");
        }
        product.setArchived(false);
        product.setActive(true);
        productRepository.save(product);
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> searchProducts(UUID storeId, String query) {
        return productRepository.search(storeId, query).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> filterProducts(UUID storeId, UUID categoryId, Boolean active, Boolean archived) {
        List<Product> products = productRepository.findByStore_Id(storeId).stream()
                .filter(p -> categoryId == null || (p.getCategory() != null && p.getCategory().getId().equals(categoryId)))
                .filter(p -> active == null || p.isActive() == active)
                .filter(p -> archived == null || p.isArchived() == archived)
                .toList();
        return products.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private String generateSku(UUID storeId) {
        long timestamp = System.currentTimeMillis();
        int random = secureRandom.nextInt(10000);
        return SKU_PREFIX + timestamp + "-" + String.format("%04d", random);
    }

    ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sellingPrice(product.getSellingPrice())
                .costPrice(product.getCostPrice())
                .wholesalePrice(product.getWholesalePrice())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .active(product.isActive())
                .archived(product.isArchived())
                .minStockLevel(product.getMinStockLevel())
                .maxStockLevel(product.getMaxStockLevel())
                .image(product.getImage())
                .batchNumber(product.getBatchNumber())
                .expiryDate(product.getExpiryDate())
                .category(product.getCategory() != null ? CategoryResponse.builder()
                        .id(product.getCategory().getId())
                        .name(product.getCategory().getName())
                        .description(product.getCategory().getDescription())
                        .build() : null)
                .supplier(product.getSupplier() != null ? SupplierResponse.builder()
                        .id(product.getSupplier().getId())
                        .name(product.getSupplier().getName())
                        .phone(product.getSupplier().getPhone())
                        .email(product.getSupplier().getEmail())
                        .address(product.getSupplier().getAddress())
                        .outstandingBalance(product.getSupplier().getOutstandingBalance())
                        .status(product.getSupplier().getStatus())
                        .createdAt(product.getSupplier().getCreatedAt())
                        .build() : null)
                .stock(product.getStock() != null ? StockResponse.builder()
                        .id(product.getStock().getId())
                        .quantity(product.getStock().getQuantity())
                        .lowStockThreshold(product.getStock().getLowStockThreshold())
                        .unit(product.getStock().getUnit())
                        .trackInventory(product.getStock().isTrackInventory())
                        .batchNumber(product.getStock().getBatchNumber())
                        .expiryDate(product.getStock().getExpiryDate())
                        .minStockLevel(product.getStock().getMinStockLevel())
                        .maxStockLevel(product.getStock().getMaxStockLevel())
                        .build() : null)
                .images(product.getImages() != null ? product.getImages().stream()
                        .map(img -> ProductImageResponse.builder()
                                .id(img.getId())
                                .url(img.getUrl())
                                .altText(img.getAltText())
                                .sortOrder(img.getSortOrder())
                                .build())
                        .toList() : List.of())
                .locations(product.getLocations() != null ? product.getLocations().stream()
                        .map(loc -> ProductLocationResponse.builder()
                                .id(loc.getId())
                                .locationType(loc.getLocationType())
                                .locationName(loc.getLocationName())
                                .quantity(loc.getQuantity())
                                .build())
                        .toList() : List.of())
                .wholesaleRules(product.getWholesaleRules() != null ? product.getWholesaleRules().stream()
                        .map(rule -> WholesalePriceRuleResponse.builder()
                                .id(rule.getId())
                                .minQuantity(rule.getMinQuantity())
                                .maxQuantity(rule.getMaxQuantity())
                                .price(rule.getPrice())
                                .build())
                        .toList() : List.of())
                .build();
    }
}
