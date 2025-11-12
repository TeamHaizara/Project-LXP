package com.example.projectlxp.service.section;

import com.example.projectlxp.controller.section.response.SectionResponse;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.section.Section;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.section.SectionRepository;
import com.example.projectlxp.service.section.dto.SectionServiceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SectionServiceImpl implements SectionService {
    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    public SectionServiceImpl(SectionRepository sectionRepository, CourseRepository courseRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    @Transactional
    public SectionResponse createSection(SectionServiceDto dto, Long userId) {
        Course course = courseRepository.findByIdAndNotDeleted(dto.courseId())
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.COURSE_NOT_FOUND)
                        .withId(dto.courseId())
                        .build());

        // 섹션 순서(order) 중복 검증
        if (sectionRepository.existsByCourseIdAndOrderAndDeletedAtIsNull(dto.courseId(), dto.order())) {
            throw BusinessException.builder(ExceptionCode.DUPLICATE_SECTION_ORDER)
                    .withId(Long.valueOf(dto.order()))
                    .build();
        }

        // Section.create() 직접 호출 대신, dto.toEntity()를 사용하도록 변경
        Section section = dto.toEntity(course);

        return SectionResponse.from(sectionRepository.save(section));
    }

    @Override
    @Transactional
    public SectionResponse updateSection(Long courseId, Long sectionId, SectionServiceDto dto, Long userId) {
        Section section = findSectionWithCourse(sectionId);
        validateInstructorAccess(section, courseId, userId);
        validateSectionBelongsToCourse(section, courseId);

        // 섹션 순서(order)를 변경하는 경우에만 중복 검증
        if (!section.getOrder().equals(dto.order())) {
            if (sectionRepository.existsByCourseIdAndOrderAndDeletedAtIsNull(section.getCourse().getId(), dto.order())) {
                throw BusinessException.builder(ExceptionCode.DUPLICATE_SECTION_ORDER)
                        .withId(Long.valueOf(dto.order()))
                        .build();
            }
        }

        section.update(dto.title(), dto.order());
        return SectionResponse.from(section);
    }

    @Override
    @Transactional
    public void deleteSection(Long courseId, Long sectionId, Long userId) {
        Section section = findSectionWithCourse(sectionId);
        validateInstructorAccess(section, courseId, userId);
        validateSectionBelongsToCourse(section, courseId);

        section.cascadeSoftDelete();
    }

    @Override
    @Transactional
    public void reorderSections(Long courseId, List<Long> sectionIds, Long userId) {
        courseRepository.findByIdAndNotDeleted(courseId)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.COURSE_NOT_FOUND)
                        .withId(courseId)
                        .build());

        List<Section> sectionsInDb = sectionRepository.findByCourseIdAndNotDeleted(courseId);
        Map<Long, Section> sectionMap = sectionsInDb.stream()
                .collect(Collectors.toMap(Section::getId, Function.identity()));

        Set<Long> dbSectionIds = sectionMap.keySet();
        Set<Long> requestSectionIds = new HashSet<>(sectionIds);

        if (!dbSectionIds.equals(requestSectionIds)) {
            throw BusinessException.builder(ExceptionCode.INVALID_SECTION_REORDER_REQUEST)
                    .build();
        }

        IntStream.range(0, sectionIds.size())
                .forEach(i -> {
                    Section section = sectionMap.get(sectionIds.get(i));
                    section.update(section.getTitle(), i + 1);
                });
    }

    private Section findSectionWithCourse(Long sectionId) {
        return sectionRepository.findByIdWithCourse(sectionId)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.SECTION_NOT_FOUND)
                        .withId(sectionId)
                        .build());
    }

    private void validateInstructorAccess(Section section, Long courseId, Long userId) {
        Course course = section.getCourse();
        if (!course.isOwnedBy(userId)) {
            throw BusinessException.builder(ExceptionCode.NOT_COURSE_INSTRUCTOR)
                    .withId(userId, courseId)
                    .build();
        }
    }

    private void validateSectionBelongsToCourse(Section section, Long expectedCourseId) {
        if (!section.getCourse().getId().equals(expectedCourseId)) {
            throw BusinessException.builder(ExceptionCode.SECTION_NOT_IN_COURSE)
                    .withId(section.getId(), expectedCourseId)
                    .build();
        }
    }
}
