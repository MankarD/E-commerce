package com.sephora.ecommerce.services;

import com.sephora.ecommerce.entities.Category;
import com.sephora.ecommerce.payloads.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);
    List<CategoryDTO> getAllCategories();
    CategoryDTO updateCategory(String categoryName, String newCategoryName);
    String deleteCategory(String identifier);
}
