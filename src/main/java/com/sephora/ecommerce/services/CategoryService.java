package com.sephora.ecommerce.services;

import com.sephora.ecommerce.entities.Category;
import com.sephora.ecommerce.payloads.CategoryDTO;
import com.sephora.ecommerce.payloads.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO updateCategory(String categoryName, String newCategoryName);
    String deleteCategory(String identifier);
}
