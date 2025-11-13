package com.example.projectlxp.service.section;

import com.example.projectlxp.controller.section.response.SectionResponse;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.section.Section;
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
import static org.mockito.ArgumentMatchers.anyLong;
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
    private final Long instructorId = 1L; // 이 강의의 소유자 ID
    private final Long otherUserId = 2L; // 소유자가 아닌 다른 사용자 ID
    private final Long invalidCourseId = 99L;

    @BeforeEach
    void setUp() {
        // instructorId가 1L인 Course 객체 생성
        course = new Course(instructorId, 1L, "테스트 코스", "설명", 10000);
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
            ReflectionTestUtils.setField(newSection, "id", 1L);

            given(courseRepository.findByIdAndNotDeleted(courseId)).willReturn(Optional.of(course));
            given(sectionRepository.existsByCourseIdAndOrderAndDeletedAtIsNull(courseId, dto.order())).willReturn(false);
            given(sectionRepository.save(any(Section.class))).willReturn(newSection);

            // when
            SectionResponse response = sectionService.createSection(dto, instructorId);

            // then
            assertThat(response.title()).isEqualTo("새로운 섹션");
            verify(courseRepository).findByIdAndNotDeleted(courseId);
            verify(sectionRepository).existsByCourseIdAndOrderAndDeletedAtIsNull(courseId, dto.order());
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
                    () -> sectionService.createSection(dto, instructorId));

            // then
            assertThat(exception.getMessage()).isEqualTo( "Course not found with id: " + invalidCourseId);
            verify(sectionRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패 - 코스 강사가 아닐 경우 예외 발생")
        void should_throwException_when_userIsNotCourseInstructor() {
            // given
            SectionServiceDto dto = new SectionServiceDto(courseId, "새로운 섹션", 1);
            given(courseRepository.findByIdAndNotDeleted(courseId)).willReturn(Optional.of(course)); // course.instructorId는 1L

            // when
            // otherUserId(2L)로 요청
            BusinessException exception = assertThrows(BusinessException.class,
                () -> sectionService.createSection(dto, otherUserId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.NOT_COURSE_INSTRUCTOR.getStatus());
            verify(sectionRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패 - 섹션 순서 중복 시 예외 발생")
        void should_throwException_when_sectionOrderIsDuplicated() {
            // given
            SectionServiceDto dto = new SectionServiceDto(courseId, "새로운 섹션", 1);

            given(courseRepository.findByIdAndNotDeleted(courseId)).willReturn(Optional.of(course));
            given(sectionRepository.existsByCourseIdAndOrderAndDeletedAtIsNull(courseId, dto.order())).willReturn(true);

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                () -> sectionService.createSection(dto, instructorId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.DUPLICATE_SECTION_ORDER.getStatus());
            verify(sectionRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("섹션 수정")
    class UpdateSection {
        private final Long sectionId = 1L;
        private final Long invalidSectionId = 99L;
        private Section existingSection;

        @BeforeEach
        void setupUpdate() {
            existingSection = Section.create(course, "원본 섹션", 1);
            ReflectionTestUtils.setField(existingSection, "id", sectionId);
            ReflectionTestUtils.setField(existingSection, "course", course);
        }

        @Test
        @DisplayName("성공")
        void should_updateSection_when_validRequest() {
            // given
            SectionServiceDto dto = new SectionServiceDto(null, "수정된 섹션", 2);

            given(sectionRepository.findByIdWithCourse(sectionId)).willReturn(Optional.of(existingSection));
            given(sectionRepository.existsByCourseIdAndOrderAndDeletedAtIsNull(courseId, dto.order())).willReturn(false);

            // when
            SectionResponse response = sectionService.updateSection(courseId, sectionId, dto, instructorId);

            // then
            assertThat(response.title()).isEqualTo("수정된 섹션");
            assertThat(response.order()).isEqualTo(2);
            verify(sectionRepository).findByIdWithCourse(sectionId);
            verify(sectionRepository).existsByCourseIdAndOrderAndDeletedAtIsNull(courseId, dto.order());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 섹션 ID로 요청 시 예외 발생")
        void should_throwException_when_sectionNotFound() {
            // given
            SectionServiceDto dto = new SectionServiceDto(null, "수정된 섹션", 2);
            given(sectionRepository.findByIdWithCourse(invalidSectionId)).willReturn(Optional.empty());

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> sectionService.updateSection(courseId, invalidSectionId, dto, instructorId));

            // then
            assertThat(exception.getMessage()).isEqualTo("Section not found with id: " + invalidSectionId);
        }

        @Test
        @DisplayName("실패 - 다른 코스 ID로 요청 시 예외 발생")
        void should_throwException_when_courseIdDoesNotMatch() {
            // given
            SectionServiceDto dto = new SectionServiceDto(null, "수정된 섹션", 2);
            Long otherCourseId = 2L;

            given(sectionRepository.findByIdWithCourse(sectionId)).willReturn(Optional.of(existingSection));

            // when / then
            BusinessException exception = assertThrows(BusinessException.class,
                () -> sectionService.updateSection(otherCourseId, sectionId, dto, instructorId));

            assertThat(exception.getMessage()).contains("does not belong to course");
            verify(sectionRepository).findByIdWithCourse(sectionId);
        }

        @Test
        @DisplayName("실패 - 코스 강사가 아닐 경우 예외 발생")
        void should_throwException_when_userIsNotCourseInstructor() {
            // given
            SectionServiceDto dto = new SectionServiceDto(null, "수정된 섹션", 2);
            given(sectionRepository.findByIdWithCourse(sectionId)).willReturn(Optional.of(existingSection));

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                () -> sectionService.updateSection(courseId, sectionId, dto, otherUserId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.NOT_COURSE_INSTRUCTOR.getStatus());
        }
    }

    @Nested
    @DisplayName("섹션 삭제")
    class DeleteSection {
        private final Long sectionId = 1L;
        private Section existingSection;

        @BeforeEach
        void setupDelete() {
            existingSection = Section.create(course, "삭제될 섹션", 1);
            ReflectionTestUtils.setField(existingSection, "id", sectionId);
            ReflectionTestUtils.setField(existingSection, "course", course);
        }

        @Test
        @DisplayName("성공")
        void should_softDeleteSection_when_validRequest() {
            // given
            given(sectionRepository.findByIdWithCourse(sectionId)).willReturn(Optional.of(existingSection));

            // when
            sectionService.deleteSection(courseId, sectionId, instructorId);

            // then
            assertThat(existingSection.isDeleted()).isTrue();
            verify(sectionRepository).findByIdWithCourse(sectionId);
        }

        @Test
        @DisplayName("실패 - 코스 강사가 아닐 경우 예외 발생")
        void should_throwException_when_userIsNotCourseInstructor() {
            // given
            given(sectionRepository.findByIdWithCourse(sectionId)).willReturn(Optional.of(existingSection));

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                () -> sectionService.deleteSection(courseId, sectionId, otherUserId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.NOT_COURSE_INSTRUCTOR.getStatus());
        }
    }

    @Nested
    @DisplayName("섹션 순서 변경")
    class ReorderSections {
        private Section section1, section2;
        private List<Section> sectionsInCourse;

        @BeforeEach
        void setupReorder() {
            section1 = Section.create(course, "섹션 1", 1);
            ReflectionTestUtils.setField(section1, "id", 1L);
            section2 = Section.create(course, "섹션 2", 2);
            ReflectionTestUtils.setField(section2, "id", 2L);
            sectionsInCourse = List.of(section1, section2);
            ReflectionTestUtils.setField(course, "sections", sectionsInCourse);
        }

        @Test
        @DisplayName("성공")
        void should_reorderSections_when_validRequest() {
            // given
            List<Long> reorderedIds = List.of(2L, 1L);
            given(courseRepository.findByIdWithSectionsForManage(courseId)).willReturn(Optional.of(course));

            // when
            sectionService.reorderSections(courseId, reorderedIds, instructorId);

            // then
            assertThat(section2.getOrder()).isEqualTo(1);
            assertThat(section1.getOrder()).isEqualTo(2);
            verify(courseRepository).findByIdWithSectionsForManage(courseId);
        }

        @Test
        @DisplayName("실패 - 코스 강사가 아닐 경우 예외 발생")
        void should_throwException_when_userIsNotCourseInstructor() {
            // given
            List<Long> reorderedIds = List.of(2L, 1L);
            given(courseRepository.findByIdWithSectionsForManage(courseId)).willReturn(Optional.of(course));

            // when
            BusinessException exception = assertThrows(BusinessException.class,
                () -> sectionService.reorderSections(courseId, reorderedIds, otherUserId));

            // then
            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.NOT_COURSE_INSTRUCTOR.getStatus());
        }

        @Test
        @DisplayName("실패 - 요청된 ID 목록이 DB의 ID 목록과 다를 때 예외 발생")
        void should_throwException_when_idSetMismatch() {
            // given
            List<Long> reorderedIds = List.of(99L, 1L); // DB에 없는 ID 포함
            given(courseRepository.findByIdWithSectionsForManage(courseId)).willReturn(Optional.of(course));

            // when / then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> sectionService.reorderSections(courseId, reorderedIds, instructorId));

            assertThat(exception.getHttpStatus()).isEqualTo(ExceptionCode.INVALID_SECTION_REORDER_REQUEST.getStatus());
        }
    }
}
