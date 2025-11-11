package com.example.projectlxp.service.category;

import com.example.projectlxp.service.category.dto.CategoryServiceDto;
import java.util.List;

public interface CategoryService {
    CategoryServiceDto createCategory(CategoryServiceDto dto);
    List<CategoryServiceDto> getAllCategories();
    void deleteCategory(Long categoryId);
}
