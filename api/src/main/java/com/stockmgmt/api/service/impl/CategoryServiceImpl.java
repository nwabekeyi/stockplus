package com.stockmgmt.api.service.impl;

import com.stockmgmt.api.entity.Category;
import com.stockmgmt.api.entity.Store;
import com.stockmgmt.api.entity.dto.response.CategoryResponse;
import com.stockmgmt.api.exception.ResourceNotFoundException;
import com.stockmgmt.api.repository.CategoryRepository;
import com.stockmgmt.api.service.StoreService;
import com.stockmgmt.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final StoreService storeService;

    @Override
    @Transactional
    public CategoryResponse createCategory(UUID storeId, String name, String description) {
        Store store = storeService.getStore(storeId);

        Category category = Category.builder()
                .name(name)
                .description(description)
                .store(store)
                .build();

        categoryRepository.save(category);
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    @Override
    public List<CategoryResponse> getCategories(UUID storeId) {
        return categoryRepository.findByStore_Id(storeId).stream()
                .map(c -> CategoryResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .description(c.getDescription())
                        .build())
                .toList();
    }
}