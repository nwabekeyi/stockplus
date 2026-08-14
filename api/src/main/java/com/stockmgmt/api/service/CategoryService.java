package com.stockmgmt.api.service;

import com.stockmgmt.api.entity.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(UUID storeId, String name, String description);
    List<CategoryResponse> getCategories(UUID storeId);
}