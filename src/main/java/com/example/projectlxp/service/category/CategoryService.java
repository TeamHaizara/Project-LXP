package com.example.projectlxp.service.category;

import com.example.projectlxp.service.category.dto.CategoryServiceDto;
import java.util.List;

public interface CategoryService {
    CategoryServiceDto createCategory(CategoryServiceDto dto);
    List<CategoryServiceDto> getAllCategories();
    CategoryServiceDto getCategoryById(Long id);
    CategoryServiceDto updateCategory(Long categoryId, CategoryServiceDto dto);
    void deleteCategory(Long categoryId);
}
