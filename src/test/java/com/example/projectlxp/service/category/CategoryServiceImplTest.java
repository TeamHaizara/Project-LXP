package com.example.projectlxp.service.category;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.category.Category;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.repository.category.CategoryRepository;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.service.category.dto.CategoryServiceDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category 서비스 로직 테스트")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CourseRepository courseRepository; // deleteCategory 테스트를 위해 필요

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Nested
    @DisplayName("카테고리 생성")
    class CreateCategory {

        @Test
        @DisplayName("성공")
        void should_createCategory_when_validRequest() {
            // given
            CategoryServiceDto dto = new CategoryServiceDto(null, "새로운 카테고리");
            Category newCategory = Category.create(dto.name());

            given(categoryRepository.existsByName(dto.name())).willReturn(false);
            given(categoryRepository.save(any(Category.class))).willReturn(newCategory);

            // when
            CategoryServiceDto response = categoryService.createCategory(dto);

            // then
            assertThat(response.name()).isEqualTo("새로운 카테고리");
            verify(categoryRepository).existsByName(dto.name());
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("실패 - 이름 중복 시 예외 발생")
        void should_throwException_when_categoryNameIsDuplicated() {
            // given
            CategoryServiceDto dto = new CategoryServiceDto(null, "중복된 이름");
            given(categoryRepository.existsByName(dto.name())).willReturn(true);

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                () -> categoryService.createCategory(dto));

            // then
            assertThat(exception.getMessage()).isEqualTo("Duplicate category name: " + dto.name());
            verify(categoryRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("모든 카테고리 조회")
    class GetAllCategories {

        @Test
        @DisplayName("성공")
        void should_returnAllCategories_when_categoriesExist() {
            // given
            List<Category> categories = List.of(
                Category.create("Java"),
                Category.create("Spring")
            );
            given(categoryRepository.findAll()).willReturn(categories);

            // when
            List<CategoryServiceDto> result = categoryService.getAllCategories();

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).name()).isEqualTo("Java");
            verify(categoryRepository).findAll();
        }

        @Test
        @DisplayName("성공 - 카테고리가 없을 경우 빈 리스트 반환")
        void should_returnEmptyList_when_noCategoriesExist() {
            // given
            given(categoryRepository.findAll()).willReturn(Collections.emptyList());

            // when
            List<CategoryServiceDto> result = categoryService.getAllCategories();

            // then
            assertThat(result).isEmpty();
            verify(categoryRepository).findAll();
        }
    }

    @Nested
    @DisplayName("ID로 카테고리 조회")
    class GetCategoryById {

        private final Long categoryId = 1L;
        private final Long invalidCategoryId = 99L;

        @Test
        @DisplayName("성공")
        void should_returnCategory_when_idExists() {
            // given
            Category category = Category.create("Java");
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

            // when
            CategoryServiceDto result = categoryService.getCategoryById(categoryId);

            // then
            assertThat(result.name()).isEqualTo("Java");
            verify(categoryRepository).findById(categoryId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 조회 시 예외 발생")
        void should_throwException_when_idDoesNotExist() {
            // given
            given(categoryRepository.findById(invalidCategoryId)).willReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.getCategoryById(invalidCategoryId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.CATEGORY_NOT_FOUND.getStatus());
            assertThat(exception.getMessage()).isEqualTo("Category not found with id: " + invalidCategoryId);
        }
    }

    @Nested
    @DisplayName("카테고리 수정")
    class UpdateCategory {

        private final Long categoryId = 1L;
        private final Long invalidCategoryId = 99L;

        @Test
        @DisplayName("성공")
        void should_updateCategory_when_validRequest() {
            // given
            Category existingCategory = Category.create("원본 이름");
            CategoryServiceDto dto = new CategoryServiceDto(null, "수정된 이름");

            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));
            given(categoryRepository.existsByName("수정된 이름")).willReturn(false);

            // when
            CategoryServiceDto result = categoryService.updateCategory(categoryId, dto);

            // then
            assertThat(result.name()).isEqualTo("수정된 이름");
            assertThat(existingCategory.getName()).isEqualTo("수정된 이름"); // 엔티티 상태 변경 검증
            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository).existsByName("수정된 이름");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 수정 요청 시 예외 발생")
        void should_throwException_when_updatingNonExistentCategory() {
            // given
            CategoryServiceDto dto = new CategoryServiceDto(null, "수정된 이름");
            given(categoryRepository.findById(invalidCategoryId)).willReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.updateCategory(invalidCategoryId, dto));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.CATEGORY_NOT_FOUND.getStatus());
        }

        @Test
        @DisplayName("실패 - 변경하려는 이름이 이미 존재할 경우 예외 발생")
        void should_throwException_when_updatedNameIsDuplicated() {
            // given
            Category existingCategory = Category.create("원본 이름");
            CategoryServiceDto dto = new CategoryServiceDto(null, "이미 있는 이름");

            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));
            given(categoryRepository.existsByName("이미 있는 이름")).willReturn(true);

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.updateCategory(categoryId, dto));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.DUPLICATE_CATEGORY_NAME.getStatus());
            assertThat(exception.getMessage()).isEqualTo("Duplicate category name: " + dto.name());
        }

        @Test
        @DisplayName("성공 - 이름을 변경하지 않을 경우 중복 검사를 수행하지 않음")
        void should_notCheckDuplicate_when_nameIsNotChanged() {
            // given
            Category existingCategory = Category.create("같은 이름");
            CategoryServiceDto dto = new CategoryServiceDto(null, "같은 이름");

            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));

            // when
            categoryService.updateCategory(categoryId, dto);

            // then
            // existsByName이 호출되지 않았음을 검증
            verify(categoryRepository, never()).existsByName(anyString());
        }
    }

    @Nested
    @DisplayName("카테고리 삭제")
    class DeleteCategory {

        private final Long categoryId = 1L;
        private final Long invalidCategoryId = 99L;

        @Test
        @DisplayName("성공")
        void should_deleteCategory_when_validRequest() {
            // given
            Category existingCategory = Category.create("삭제될 카테고리");
            given(courseRepository.findByCategoryIdAndNotDeleted(categoryId)).willReturn(Collections.emptyList());
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));

            // when
            categoryService.deleteCategory(categoryId);

            // then
            verify(courseRepository).findByCategoryIdAndNotDeleted(categoryId);
            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository).delete(existingCategory);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID로 요청 시 예외 발생")
        void should_throwException_when_categoryIdNotFound() {
            // given
            given(courseRepository.findByCategoryIdAndNotDeleted(invalidCategoryId)).willReturn(Collections.emptyList());
            given(categoryRepository.findById(invalidCategoryId)).willReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                () -> categoryService.deleteCategory(invalidCategoryId));

            // then
            assertThat(exception.getMessage()).isEqualTo("Category not found with id: " + invalidCategoryId);
            verify(categoryRepository, never()).delete(any());
        }

        @Test
        @DisplayName("실패 - 하위 강좌가 존재할 경우 예외 발생")
        void should_throwException_when_categoryHasCourses() {
            // given
            // Course 객체를 직접 생성하는 대신, Mockito.mock()을 사용하여 가짜 객체를 생성
            Course mockCourse = mock(Course.class);
            // CourseRepository가 비어있지 않은 리스트(가짜 Course 객체를 포함)를 반환하도록 설정
            given(courseRepository.findByCategoryIdAndNotDeleted(categoryId)).willReturn(List.of(mockCourse));

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                () -> categoryService.deleteCategory(categoryId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.CATEGORY_HAS_COURSES.getStatus());
            assertThat(exception.getMessage()).contains("because it has associated courses");
            verify(categoryRepository, never()).findById(any());
            verify(categoryRepository, never()).delete(any());
        }
    }
}
