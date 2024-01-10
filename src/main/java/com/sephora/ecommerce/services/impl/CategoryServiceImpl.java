package com.sephora.ecommerce.services.impl;

import com.sephora.ecommerce.entities.Category;
import com.sephora.ecommerce.exceptions.APIException;
import com.sephora.ecommerce.payloads.CategoryDTO;
import com.sephora.ecommerce.repositories.CategoryRepository;
import com.sephora.ecommerce.services.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if (savedCategory != null) {
            throw new APIException("Category with the name '" + category.getCategoryName() + "' already exists!!!");
        }
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOs = categories.stream().map(category -> modelMapper.map(category, CategoryDTO.class)).collect(Collectors.toList());
        return categoryDTOs;
    }

    @Override
    public CategoryDTO updateCategory(String categoryName, String newCategoryName) {
        Category savedCategory = categoryRepository.findByCategoryName(categoryName);

        if (savedCategory == null) {
            throw new APIException("Category with the name '" + categoryName + "' does not exist!!!");
        }

        if (!newCategoryName.equals(categoryName) && categoryRepository.existsByCategoryName(newCategoryName)) {
            throw new APIException("Category with the name '" + newCategoryName + "' already exists!!!");
        }

        savedCategory.setCategoryName(newCategoryName);
        Category updatedCategory = categoryRepository.save(savedCategory);
        return modelMapper.map(updatedCategory, CategoryDTO.class);
    }

    @Override
    public String deleteCategory(String identifier) {
        Category categoryToDelete = categoryRepository.findByCategoryName(identifier);
        if (categoryToDelete != null) {
            categoryRepository.delete(categoryToDelete);
            return "Category with Name '" + identifier + "' and ID '"+categoryToDelete.getCategoryId()+"' deleted Successfully!";
        }

        try{
            Long categoryId = Long.parseLong(identifier);
            categoryToDelete = categoryRepository.findById(categoryId).orElse(null);
            if (categoryToDelete != null) {
                categoryRepository.delete(categoryToDelete);
                return "Category with Name '"+categoryToDelete.getCategoryName()+"' and ID '" + categoryId + "' deleted Successfully!";
            }
        } catch (NumberFormatException e) {
            log.error("The {} cannot be parsed as Long, it's neither categoryName nor categoryId; exception {}", identifier, e);
        }

        throw new APIException("Category with name/id '" + identifier + "' does not exist!");
    }
}
