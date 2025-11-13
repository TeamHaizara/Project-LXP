package com.example.projectlxp.service.category;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.category.Category;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.user.Role;
import com.example.projectlxp.model.user.User;
import com.example.projectlxp.repository.category.CategoryRepository;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.user.UserRepository;
import com.example.projectlxp.service.category.dto.CategoryServiceDto;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Category 서비스 로직 테스트")
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository; // UserRepository 목 추가

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private final Long instructorId = 1L;
    private User instructor;

    @BeforeEach
    void setUp() {
        // 강사 역할을 가진 사용자 목 설정
        instructor = new User("instructor@example.com", "password", "강사님", "Java", List.of(Role.ROLE_INSTRUCTOR));
    }

    @Nested
    @DisplayName("카테고리 생성")
    class CreateCategory {

        @Test
        @DisplayName("성공")
        void should_createCategory_when_validRequest() {
            // given
            CategoryServiceDto dto = new CategoryServiceDto(null, "새로운 카테고리");
            Category newCategory = Category.create(dto.name());

            given(userRepository.findById(instructorId)).willReturn(Optional.of(instructor));
            given(categoryRepository.existsByName(dto.name())).willReturn(false);
            given(categoryRepository.save(any(Category.class))).willReturn(newCategory);

            // when
            CategoryServiceDto response = categoryService.createCategory(dto, instructorId);

            // then
            assertThat(response.name()).isEqualTo("새로운 카테고리");
            verify(userRepository).findById(instructorId);
            verify(categoryRepository).existsByName(dto.name());
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("실패 - 이름 중복 시 예외 발생")
        void should_throwException_when_categoryNameIsDuplicated() {
            // given
            CategoryServiceDto dto = new CategoryServiceDto(null, "중복된 이름");
            given(userRepository.findById(instructorId)).willReturn(Optional.of(instructor));
            given(categoryRepository.existsByName(dto.name())).willReturn(true);

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                () -> categoryService.createCategory(dto, instructorId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.DUPLICATE_CATEGORY_NAME.getStatus());
            verify(categoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패 - 강사 권한이 없을 경우 예외 발생")
        void should_throwException_when_userIsNotInstructor() {
            // given
            Long learnerId = 2L;
            User learner = new User("learner@example.com", "password", "수강생", "Java", List.of(Role.ROLE_LEARNER));
            CategoryServiceDto dto = new CategoryServiceDto(null, "새로운 카테고리");

            given(userRepository.findById(learnerId)).willReturn(Optional.of(learner));

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                () -> categoryService.createCategory(dto, learnerId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.USER_NOT_AUTHORITY.getStatus());
            verify(categoryRepository, never()).existsByName(anyString());
            verify(categoryRepository, never()).save(any());
        }
    }

    // ... (getAllCategories, getCategoryById 테스트는 변경 없음)
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

        @Test
        @DisplayName("성공")
        void should_updateCategory_when_validRequest() {
            // given
            Category existingCategory = Category.create("원본 이름");
            CategoryServiceDto dto = new CategoryServiceDto(null, "수정된 이름");

            given(userRepository.findById(instructorId)).willReturn(Optional.of(instructor));
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));
            given(categoryRepository.existsByName("수정된 이름")).willReturn(false);

            // when
            CategoryServiceDto result = categoryService.updateCategory(categoryId, dto, instructorId);

            // then
            assertThat(result.name()).isEqualTo("수정된 이름");
            assertThat(existingCategory.getName()).isEqualTo("수정된 이름");
            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository).existsByName("수정된 이름");
        }

        @Test
        @DisplayName("실패 - 변경하려는 이름이 이미 존재할 경우 예외 발생")
        void should_throwException_when_updatedNameIsDuplicated() {
            // given
            Category existingCategory = Category.create("원본 이름");
            CategoryServiceDto dto = new CategoryServiceDto(null, "이미 있는 이름");

            given(userRepository.findById(instructorId)).willReturn(Optional.of(instructor));
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));
            given(categoryRepository.existsByName("이미 있는 이름")).willReturn(true);

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> categoryService.updateCategory(categoryId, dto, instructorId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.DUPLICATE_CATEGORY_NAME.getStatus());
        }

        @Test
        @DisplayName("실패 - 이름을 변경하지 않을 경우 예외 발생")
        void should_throwException_when_nameIsNotChanged() {
            // given
            Category existingCategory = Category.create("같은 이름");
            CategoryServiceDto dto = new CategoryServiceDto(null, "같은 이름");

            given(userRepository.findById(instructorId)).willReturn(Optional.of(instructor));
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                () -> categoryService.updateCategory(categoryId, dto, instructorId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.CATEGORY_NAME_UNCHANGED.getStatus());
            verify(categoryRepository, never()).existsByName(anyString());
        }
    }

    @Nested
    @DisplayName("카테고리 삭제")
    class DeleteCategory {

        private final Long categoryId = 1L;

        @Test
        @DisplayName("성공")
        void should_deleteCategory_when_validRequest() {
            // given
            Category existingCategory = Category.create("삭제될 카테고리");
            given(userRepository.findById(instructorId)).willReturn(Optional.of(instructor));
            given(courseRepository.findByCategoryIdAndPublished(categoryId)).willReturn(Collections.emptyList());
            given(categoryRepository.findById(categoryId)).willReturn(Optional.of(existingCategory));

            // when
            categoryService.deleteCategory(categoryId, instructorId);

            // then
            verify(courseRepository).findByCategoryIdAndPublished(categoryId);
            verify(categoryRepository).findById(categoryId);
            verify(categoryRepository).delete(existingCategory);
        }

        @Test
        @DisplayName("실패 - 하위 강좌가 존재할 경우 예외 발생")
        void should_throwException_when_categoryHasCourses() {
            // given
            Course mockCourse = mock(Course.class);
            given(userRepository.findById(instructorId)).willReturn(Optional.of(instructor));
            given(courseRepository.findByCategoryIdAndPublished(categoryId)).willReturn(List.of(mockCourse));

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                () -> categoryService.deleteCategory(categoryId, instructorId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.CATEGORY_HAS_COURSES.getStatus());
            verify(categoryRepository, never()).findById(any());
            verify(categoryRepository, never()).delete(any());
        }
    }
}
