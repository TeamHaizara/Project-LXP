package com.example.projectlxp.controller.category;

import com.example.projectlxp.controller.category.request.CategoryRequest;
import com.example.projectlxp.controller.category.response.CategoryResponse;
import com.example.projectlxp.service.category.CategoryService;
import com.example.projectlxp.service.category.dto.CategoryServiceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest request) {
        // Service 계층을 호출하여 CategoryServiceDto를 받음
        CategoryServiceDto createdCategoryDto = categoryService.createCategory(request.toDto());
        // CategoryServiceDto를 CategoryResponse로 변환하여 클라이언트에 반환
        CategoryResponse response = CategoryResponse.from(createdCategoryDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        // TODO <List<CategoryResponse>> 처럼 리스트의 형태로 반환하는것보다 개별 DTO를 반환하는게 더 좋음
        // Service 계층을 호출하여 CategoryServiceDto 리스트를 받음
        List<CategoryServiceDto> categoryDtos = categoryService.getAllCategories();
        // CategoryServiceDto 리스트를 CategoryResponse 리스트로 변환
        List<CategoryResponse> responses = categoryDtos.stream()
            .map(CategoryResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        // TODO
        return null;
    }

    // update??

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
