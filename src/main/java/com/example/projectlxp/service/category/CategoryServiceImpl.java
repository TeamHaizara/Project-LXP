package com.example.projectlxp.service.category;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.category.Category;
import com.example.projectlxp.model.user.Role;
import com.example.projectlxp.model.user.User;
import com.example.projectlxp.repository.category.CategoryRepository;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.user.UserRepository;
import com.example.projectlxp.service.category.dto.CategoryServiceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CategoryServiceDto createCategory(CategoryServiceDto dto, Long userId) {
        validateInstructorRole(userId);
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
    public CategoryServiceDto updateCategory(Long id, CategoryServiceDto dto, Long userId) {
        validateInstructorRole(userId);
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> BusinessException.builder(ExceptionCode.CATEGORY_NOT_FOUND).withId(id).build());

        validateNameOnUpdate(category, dto.name());

        category.update(dto.name());
        return new CategoryServiceDto(category.getId(), category.getName());
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        validateInstructorRole(userId);
        validateCategoryHasNoCourses(categoryId);

        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> BusinessException.builder(ExceptionCode.CATEGORY_NOT_FOUND).withId(categoryId).build());

        categoryRepository.delete(category);
    }

    private void validateInstructorRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.USER_NOT_FOUND).withId(userId).build());
        if (!user.getRoles().contains(Role.ROLE_INSTRUCTOR)) {
            throw BusinessException.builder(ExceptionCode.USER_NOT_AUTHORITY)
                    .withField("ROLE_INSTRUCTOR")
                    .build();
        }
    }

    private void validateNameOnUpdate(Category category, String newName) {
        if (category.getName().equals(newName)) {
            throw BusinessException.builder(ExceptionCode.CATEGORY_NAME_UNCHANGED)
                .withField(newName)
                .build();
        }
        // 이름이 변경되었을 경우에만 중복 검사를 수행
        validateNameIsUnique(newName);
    }

    private void validateNameIsUnique(String name) {
        if (categoryRepository.existsByName(name)) {
            throw BusinessException.builder(ExceptionCode.DUPLICATE_CATEGORY_NAME)
                .withField(name)
                .build();
        }
    }

    private void validateCategoryHasNoCourses(Long categoryId) {
        if (!courseRepository.findByCategoryIdAndPublished(categoryId).isEmpty()) {
            throw BusinessException.builder(ExceptionCode.CATEGORY_HAS_COURSES)
                    .withId(categoryId)
                    .build();
        }
    }
}
