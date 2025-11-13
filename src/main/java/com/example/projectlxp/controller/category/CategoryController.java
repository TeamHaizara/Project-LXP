package com.example.projectlxp.controller.category;

import com.example.projectlxp.controller.category.request.CategoryRequest;
import com.example.projectlxp.controller.category.response.CategoryResponse;
import com.example.projectlxp.service.category.CategoryService;
import com.example.projectlxp.service.category.dto.CategoryServiceDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api") // 기본 경로를 /api로 변경
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 강사 전용 API
    @PostMapping("/instructor/categories")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryServiceDto createdCategoryDto = categoryService.createCategory(request.toDto());
        CategoryResponse response = CategoryResponse.from(createdCategoryDto);
        return ResponseEntity.ok(response);
    }

    // 모든 사용자용 API
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryServiceDto> categoryDtos = categoryService.getAllCategories();
        List<CategoryResponse> responses = categoryDtos.stream()
            .map(CategoryResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // 모든 사용자용 API
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        CategoryServiceDto categoryDto = categoryService.getCategoryById(id);
        CategoryResponse response = CategoryResponse.from(categoryDto);
        return ResponseEntity.ok(response);
    }

    // 강사 전용 API
    @PutMapping("/instructor/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                           @Valid @RequestBody CategoryRequest request) {
        CategoryServiceDto updatedDto = categoryService.updateCategory(id, request.toDto());
        CategoryResponse response = CategoryResponse.from(updatedDto);
        return ResponseEntity.ok(response);
    }

    // 강사 전용 API
    @DeleteMapping("/instructor/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
