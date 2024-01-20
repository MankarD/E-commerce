package com.sephora.ecommerce.controllers;

import com.sephora.ecommerce.config.AppConstants;
import com.sephora.ecommerce.payloads.CategoryDTO;
import com.sephora.ecommerce.payloads.CategoryResponse;
import com.sephora.ecommerce.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/admin/category")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_CATEGORIES_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder
    ){
        CategoryResponse categoryDTOs = categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(categoryDTOs, HttpStatus.FOUND);
    }

    @PatchMapping("/admin/categories/{categoryName}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable String categoryName, @RequestParam String newCategoryName){
        CategoryDTO updatedCategoryDTO = categoryService.updateCategory(categoryName, newCategoryName);
        return new ResponseEntity<>(updatedCategoryDTO, HttpStatus.OK);
    }

    // Delete by categoryId or categoryName
    @DeleteMapping("/admin/categories/{identifier}")
    public ResponseEntity<String> deleteCategory(@PathVariable String identifier) {
        String responseMessage = categoryService.deleteCategory(identifier);
        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

}
