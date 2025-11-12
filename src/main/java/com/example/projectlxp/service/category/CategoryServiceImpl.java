package com.example.projectlxp.service.category;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.category.Category;
import com.example.projectlxp.repository.category.CategoryRepository;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.service.category.dto.CategoryServiceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public CategoryServiceDto createCategory(CategoryServiceDto dto) {
        validateNameIsUnique(dto.name());

        Category savedCategory = categoryRepository.save(dto.toEntity());

        return new CategoryServiceDto(savedCategory.getId(), savedCategory.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryServiceDto> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(category -> new CategoryServiceDto(category.getId(), category.getName()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryServiceDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> BusinessException.builder(ExceptionCode.CATEGORY_NOT_FOUND).withId(id).build());
        return new CategoryServiceDto(category.getId(), category.getName());
    }

    @Override
    @Transactional
    public CategoryServiceDto updateCategory(Long id, CategoryServiceDto dto) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> BusinessException.builder(ExceptionCode.CATEGORY_NOT_FOUND).withId(id).build());

        validateNameOnUpdate(category, dto.name());

        category.update(dto.name());
        return new CategoryServiceDto(category.getId(), category.getName());
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        validateCategoryHasNoCourses(categoryId);

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> BusinessException.builder(ExceptionCode.CATEGORY_NOT_FOUND).withId(categoryId).build());

        categoryRepository.delete(category);
    }

    /**
     * 카테고리 수정 시 이름의 유효성을 검증하는 헬퍼 메소드
     * @param category 수정할 대상 카테고리 엔티티
     * @param newName 새로운 카테고리 이름
     */
    private void validateNameOnUpdate(Category category, String newName) {
        // 이름이 변경되었을 경우에만 중복 검사를 수행
        if (!category.getName().equals(newName)) {
            validateNameIsUnique(newName);
        }
    }

    /**
     * 카테고리 이름이 중복되는지 검증하는 헬퍼 메소드
     * @param name 검증할 카테고리 이름
     */
    private void validateNameIsUnique(String name) {
        if (categoryRepository.existsByName(name)) {
            throw BusinessException.builder(ExceptionCode.DUPLICATE_CATEGORY_NAME)
                .withField(name)
                .build();
        }
    }

    /**
     * 카테고리에 속한 강좌가 없는지 검증하는 헬퍼 메소드
     * @param categoryId 검증할 카테고리 ID
     */
    private void validateCategoryHasNoCourses(Long categoryId) {
        if (!courseRepository.findByCategoryIdAndNotDeleted(categoryId).isEmpty()) {
            throw BusinessException.builder(ExceptionCode.CATEGORY_HAS_COURSES)
                .withId(categoryId)
                .build();
        }
    }
}
