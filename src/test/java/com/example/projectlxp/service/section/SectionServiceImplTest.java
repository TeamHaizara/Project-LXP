package com.example.projectlxp.service.section;

import com.example.projectlxp.controller.section.response.SectionResponse;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.section.Section;
import com.example.projectlxp.model.section.exception.SectionNotFoundException;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.section.SectionRepository;
import com.example.projectlxp.service.section.dto.SectionServiceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Section 서비스 로직 테스트")
class SectionServiceImplTest {

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private SectionServiceImpl sectionService;

    private Course course;
    private final Long courseId = 1L;
    private final Long invalidCourseId = 99L;

    @BeforeEach
    void setUp() {
        course = new Course(1L, 1L, "테스트 코스", "설명", 10000);
        ReflectionTestUtils.setField(course, "id", courseId);
    }

    @Nested
    @DisplayName("섹션 생성")
    class CreateSection {

        @Test
        @DisplayName("성공")
        void should_createSection_when_validRequest() {
            // given
            SectionServiceDto dto = new SectionServiceDto(courseId, "새로운 섹션", 1);
            Section newSection = Section.create(course, dto.title(), dto.order());

            given(courseRepository.findByIdAndNotDeleted(courseId)).willReturn(Optional.of(course));
            given(sectionRepository.save(any(Section.class))).willReturn(newSection);

            // when
            SectionResponse response = sectionService.createSection(dto);

            // then
            assertThat(response.title()).isEqualTo("새로운 섹션");
            verify(courseRepository).findByIdAndNotDeleted(courseId);
            verify(sectionRepository).save(any(Section.class));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 코스 ID로 요청 시 예외 발생")
        void should_throwException_when_courseNotFound() {
            // given
            SectionServiceDto dto = new SectionServiceDto(invalidCourseId, "새로운 섹션", 1);
            given(courseRepository.findByIdAndNotDeleted(invalidCourseId)).willReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> sectionService.createSection(dto));

            // then
            assertThat(exception.getMessage()).isEqualTo( "Course not found with id: " + invalidCourseId);
            verify(sectionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("섹션 수정")
    class UpdateSection {
        private final Long sectionId = 1L;
        private final Long invalidSectionId = 99L;

        @Test
        @DisplayName("성공")
        void should_updateSection_when_validRequest() {
            // given
            SectionServiceDto dto = new SectionServiceDto(null, "수정된 섹션", 2);
            Section existingSection = Section.create(course, "원본 섹션", 1);

            given(sectionRepository.findByIdAndDeletedAtIsNull(sectionId)).willReturn(Optional.of(existingSection));

            // when
            SectionResponse response = sectionService.updateSection(sectionId, dto);

            // then
            assertThat(response.title()).isEqualTo("수정된 섹션");
            assertThat(response.order()).isEqualTo(2);
            assertThat(existingSection.getTitle()).isEqualTo("수정된 섹션");
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 섹션 ID로 요청 시 예외 발생")
        void should_throwException_when_sectionNotFound() {
            // given
            SectionServiceDto dto = new SectionServiceDto(null, "수정된 섹션", 2);
            given(sectionRepository.findByIdAndDeletedAtIsNull(invalidSectionId)).willReturn(Optional.empty());

            // when
            SectionNotFoundException exception = assertThrows(SectionNotFoundException.class,
                    () -> sectionService.updateSection(invalidSectionId, dto));

            // then
            assertThat(exception.getMessage()).isEqualTo("Section not found with id: " + invalidSectionId);
        }
    }

    @Nested
    @DisplayName("섹션 삭제")
    class DeleteSection {
        private final Long sectionId = 1L;
        private final Long invalidSectionId = 99L;

        @Test
        @DisplayName("성공")
        void should_softDeleteSection_when_validRequest() {
            // given
            Section existingSection = Section.create(course, "삭제될 섹션", 1);
            given(sectionRepository.findByIdAndDeletedAtIsNull(sectionId)).willReturn(Optional.of(existingSection));

            // when
            sectionService.deleteSection(sectionId);

            // then
            assertThat(existingSection.isDeleted()).isTrue();
            verify(sectionRepository).findByIdAndDeletedAtIsNull(sectionId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 섹션 ID로 요청 시 예외 발생")
        void should_throwException_when_sectionNotFound() {
            // given
            given(sectionRepository.findByIdAndDeletedAtIsNull(invalidSectionId)).willReturn(Optional.empty());

            // when
            SectionNotFoundException exception = assertThrows(SectionNotFoundException.class,
                    () -> sectionService.deleteSection(invalidSectionId));

            // then
            assertThat(exception.getMessage()).isEqualTo("Section not found with id: " + invalidSectionId);
        }
    }

    @Nested
    @DisplayName("섹션 순서 변경")
    class ReorderSections {
        @Test
        @DisplayName("성공")
        void should_reorderSections_when_validRequest() {
            // given
            Section section1 = Section.create(course, "섹션 1", 1);
            ReflectionTestUtils.setField(section1, "id", 1L);
            Section section2 = Section.create(course, "섹션 2", 2);
            ReflectionTestUtils.setField(section2, "id", 2L);

            List<Long> reorderedIds = List.of(2L, 1L);
            given(courseRepository.findByIdAndNotDeleted(courseId)).willReturn(Optional.of(course));
            given(sectionRepository.findByCourseIdAndNotDeleted(courseId)).willReturn(List.of(section1, section2));

            // when
            sectionService.reorderSections(courseId, reorderedIds);

            // then
            assertThat(section2.getOrder()).isEqualTo(1);
            assertThat(section1.getOrder()).isEqualTo(2);
            verify(courseRepository).findByIdAndNotDeleted(courseId);
            verify(sectionRepository).findByCourseIdAndNotDeleted(courseId);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 코스 ID로 요청 시 예외 발생")
        void should_throwException_when_courseNotFound() {
            // given
            List<Long> reorderedIds = List.of(2L, 1L);
            given(courseRepository.findByIdAndNotDeleted(invalidCourseId)).willReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> sectionService.reorderSections(invalidCourseId, reorderedIds));

            // then
            assertThat(exception.getMessage()).isEqualTo("Course not found with id: " + invalidCourseId);
        }
    }
}
