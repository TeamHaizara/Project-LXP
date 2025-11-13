package com.example.projectlxp.service.category;

import com.example.projectlxp.service.category.dto.CategoryServiceDto;
import java.util.List;

public interface CategoryService {
    CategoryServiceDto createCategory(CategoryServiceDto dto, Long userId);
    List<CategoryServiceDto> getAllCategories();
    CategoryServiceDto getCategoryById(Long id);
    CategoryServiceDto updateCategory(Long id, CategoryServiceDto dto, Long userId);
    void deleteCategory(Long categoryId, Long userId);
}
