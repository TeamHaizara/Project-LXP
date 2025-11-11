package com.example.projectlxp.service.section;

import com.example.projectlxp.controller.section.response.SectionResponse;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.section.Section;
import com.example.projectlxp.model.section.exception.SectionNotFoundException;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.section.SectionRepository;
import com.example.projectlxp.service.section.dto.SectionServiceDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public SectionResponse createSection(SectionServiceDto dto) {
        Course course = courseRepository.findByIdAndNotDeleted(dto.courseId())
            .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, dto.courseId()));

        // 섹션 순서(order) 중복 검증
        if (sectionRepository.existsByCourseIdAndOrderAndDeletedAtIsNull(dto.courseId(), dto.order())) {
            throw new BusinessException(ExceptionCode.DUPLICATE_SECTION_ORDER, Long.valueOf(dto.order()));
        }

        Section section = Section.create(course, dto.title(), dto.order());

        return SectionResponse.from(sectionRepository.save(section));
    }

    @Override
    @Transactional
    public SectionResponse updateSection(Long courseId, Long sectionId, SectionServiceDto dto) {
        Section section = sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
            .orElseThrow(() -> new SectionNotFoundException(sectionId));

        // courseId 일치 검증
        validateSectionBelongsToCourse(section, courseId);

        // 섹션 순서(order)를 변경하는 경우에만 중복 검증
        if (!section.getOrder().equals(dto.order())) {
            if (sectionRepository.existsByCourseIdAndOrderAndDeletedAtIsNull(section.getCourse().getId(), dto.order())) {
                throw new BusinessException(ExceptionCode.DUPLICATE_SECTION_ORDER, Long.valueOf(dto.order()));
            }
        }

        section.update(dto.title(), dto.order());
        return SectionResponse.from(section);
    }

    @Override
    @Transactional
    public void deleteSection(Long courseId, Long sectionId) {
        Section section = sectionRepository.findByIdAndDeletedAtIsNull(sectionId)
            .orElseThrow(() -> new SectionNotFoundException(sectionId));

        // courseId 일치 검증
        validateSectionBelongsToCourse(section, courseId);

        section.cascadeSoftDelete();
    }

    @Override
    @Transactional
    public void reorderSections(Long courseId, List<Long> sectionIds) {
        Course course = courseRepository.findByIdAndNotDeleted(courseId)
            .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, courseId));

        List<Section> sectionsInDb = sectionRepository.findByCourseIdAndNotDeleted(courseId);
        Map<Long, Section> sectionMap = sectionsInDb.stream()
            .collect(Collectors.toMap(Section::getId, Function.identity()));

        // --- 검증 로직 시작 ---

        // 1. 요청된 sectionIds의 개수와 DB에 있는 섹션의 개수가 일치하는지 확인
        if (sectionMap.size() != sectionIds.size()) {
            throw new BusinessException(ExceptionCode.INVALID_SECTION_REORDER_REQUEST,
                String.format("The number of sections does not match. Expected: %d, Actual: %d", sectionMap.size(), sectionIds.size()));
        }

        // 2. 요청된 sectionIds에 중복이 있는지, 그리고 DB에 있는 모든 섹션 ID를 포함하는지 확인
        Set<Long> dbSectionIds = sectionMap.keySet();
        Set<Long> requestSectionIds = new HashSet<>(sectionIds);

        if (requestSectionIds.size() != sectionIds.size()) {
            throw new BusinessException(ExceptionCode.INVALID_SECTION_REORDER_REQUEST, "Duplicate section IDs are not allowed.");
        }

        if (!dbSectionIds.equals(requestSectionIds)) {
            throw new BusinessException(ExceptionCode.INVALID_SECTION_REORDER_REQUEST, "The provided section IDs do not match the sections in the course.");
        }

        // --- 검증 로직 끝 ---

        // 순서 업데이트 로직
        for (int i = 0; i < sectionIds.size(); i++) {
            Long sectionId = sectionIds.get(i);
            Section section = sectionMap.get(sectionId);
            // 위에서 모든 ID가 유효함을 검증했으므로 section은 항상 존재함
            section.update(section.getTitle(), i + 1);
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
