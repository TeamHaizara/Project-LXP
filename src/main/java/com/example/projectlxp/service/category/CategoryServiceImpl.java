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
        // 카테고리 이름 중복 검사
        if (categoryRepository.existsByName(dto.name())) {
            throw BusinessException.builder(ExceptionCode.DUPLICATE_CATEGORY_NAME)
                .withField(dto.name())
                .build();
        }

        // DTO에서 toEntity 메소드를 사용하여 엔티티 생성
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

        // 이름이 변경되었고, 변경된 이름이 이미 존재하면 예외 발생
        if (!category.getName().equals(dto.name()) && categoryRepository.existsByName(dto.name())) {
            throw BusinessException.builder(ExceptionCode.DUPLICATE_CATEGORY_NAME)
                .withField(dto.name())
                .build();
        }

        category.update(dto.name());
        // 변경 감지(Dirty Checking)에 의해 트랜잭션 종료 시 자동으로 update 쿼리가 실행됩니다.
        return new CategoryServiceDto(category.getId(), category.getName());
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        // 해당 카테고리에 속한 강좌가 있는지 확인
        if (!courseRepository.findByCategoryIdAndNotDeleted(categoryId).isEmpty()) {
            throw BusinessException.builder(ExceptionCode.CATEGORY_HAS_COURSES)
                .withId(categoryId)
                .build();
        }

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> BusinessException.builder(ExceptionCode.CATEGORY_NOT_FOUND).withId(categoryId).build());

        categoryRepository.delete(category);
    }
}
