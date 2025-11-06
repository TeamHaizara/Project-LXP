package com.example.projectlxp.service.section;

import com.example.projectlxp.Exception.CourseNotFoundException;
import com.example.projectlxp.Exception.SectionNotFoundException;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.section.Section;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.section.SectionRepository;
import com.example.projectlxp.service.section.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    public SectionService(SectionRepository sectionRepository, CourseRepository courseRepository) {
        this.sectionRepository = sectionRepository;
        this.courseRepository = courseRepository;
    }

    // 섹션 생성
    @Transactional
    public SectionResponseDTO createSection(SectionCreateRequestDTO requestDTO) {
        Course course = courseRepository.findByIdAndNotDeleted(requestDTO.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(requestDTO.getCourseId()));

        // order가 지정되지 않으면 자동으로 마지막에 추가
        Integer order = requestDTO.getOrder();
        if (order == null) {
            order = sectionRepository.findMaxOrderByCourseId(requestDTO.getCourseId())
                    .orElse(0) + 1;
        }

        Section section = new Section(course, requestDTO.getTitle(), order);
        Section savedSection = sectionRepository.save(section);
        return SectionResponseDTO.from(savedSection);
    }

    // 섹션 조회 (ID)
    public SectionWithLecturesResponseDTO getSectionById(Long id) {
        Section section = sectionRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new SectionNotFoundException(id));
        return SectionWithLecturesResponseDTO.from(section);
    }

    // 특정 코스의 모든 섹션 조회
    public List<SectionResponseDTO> getSectionsByCourse(Long courseId) {
        // 코스 존재 확인
        courseRepository.findByIdAndNotDeleted(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        return sectionRepository.findByCourseIdAndNotDeletedOrderByOrder(courseId).stream()
                .map(SectionResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 특정 코스의 모든 섹션 조회 (렉처 포함)
    public List<SectionWithLecturesResponseDTO> getSectionsWithLecturesByCourse(Long courseId) {
        courseRepository.findByIdAndNotDeleted(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        return sectionRepository.findByCourseIdAndNotDeletedOrderByOrder(courseId).stream()
                .map(SectionWithLecturesResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 섹션 수정
    @Transactional
    public SectionResponseDTO updateSection(Long id, SectionUpdateRequestDTO requestDTO) {
        Section section = sectionRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new SectionNotFoundException(id));

        if (requestDTO.getTitle() != null) {
            section.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getOrder() != null) {
            section.setOrder(requestDTO.getOrder());
        }

        Section updatedSection = sectionRepository.save(section);
        return SectionResponseDTO.from(updatedSection);
    }

    // 섹션 삭제 (Soft Delete)
    @Transactional
    public void deleteSection(Long id) {
        Section section = sectionRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new SectionNotFoundException(id));

        // Soft delete: Section과 하위 Lecture도 모두 soft delete
        section.softDelete();
        section.getLectures().forEach(lecture -> lecture.softDelete());

        sectionRepository.save(section);
    }

    // 섹션 순서 변경
    @Transactional
    public void reorderSections(Long courseId, List<Long> sectionIds) {
        courseRepository.findByIdAndNotDeleted(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        for (int i = 0; i < sectionIds.size(); i++) {
            Long sectionId = sectionIds.get(i);
            Section section = sectionRepository.findByIdAndNotDeleted(sectionId)
                    .orElseThrow(() -> new SectionNotFoundException(sectionId));
            
            section.setOrder(i + 1);
            sectionRepository.save(section);
        }
    }
}
