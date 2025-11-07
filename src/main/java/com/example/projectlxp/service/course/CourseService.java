package com.example.projectlxp.service.course;

import com.example.projectlxp.Exception.CourseNotFoundException;
import com.example.projectlxp.Exception.InvalidCourseStatusException;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.service.course.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // 코스 생성
    @Transactional
    public CourseDetailResponseDTO createCourse(CourseCreateRequestDTO requestDTO) {
        Course course = new Course(
                requestDTO.getInstructorId(),
                requestDTO.getCategoryId(),
                requestDTO.getTitle(),
                requestDTO.getDescription(),
                requestDTO.getPrice()
        );

        Course savedCourse = courseRepository.save(course);
        return CourseDetailResponseDTO.from(savedCourse);
    }

    // 코스 조회 (ID)
    public CourseDetailResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        return CourseDetailResponseDTO.from(course);
    }

    // 모든 코스 조회
    public List<CourseListResponseDTO> getAllCourses() {
        return courseRepository.findAllNotDeleted().stream()
                .map(CourseListResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 강사별 코스 조회
    public List<CourseListResponseDTO> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorIdAndNotDeleted(instructorId).stream()
                .map(CourseListResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 카테고리별 코스 조회
    public List<CourseListResponseDTO> getCoursesByCategory(Long categoryId) {
        return courseRepository.findByCategoryIdAndNotDeleted(categoryId).stream()
                .map(CourseListResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 상태별 코스 조회
    public List<CourseListResponseDTO> getCoursesByStatus(CourseStatus status) {
        return courseRepository.findByStatusAndNotDeleted(status).stream()
                .map(CourseListResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 제목 검색
    public List<CourseListResponseDTO> searchCoursesByTitle(String keyword) {
        return courseRepository.searchByTitleAndNotDeleted(keyword).stream()
                .map(CourseListResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 여러 ID로 코스 목록 조회 (수강 중인 코스 목록용)
    public List<CourseListResponseDTO> getCoursesByIds(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return List.of();
        }
        
        return courseRepository.findByIdsAndNotDeleted(courseIds).stream()
                .map(CourseListResponseDTO::from)
                .collect(Collectors.toList());
    }

    // 코스 수정
    @Transactional
    public CourseDetailResponseDTO updateCourse(Long id, CourseUpdateRequestDTO requestDTO) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        if (requestDTO.getCategoryId() != null) {
            course.setCategoryId(requestDTO.getCategoryId());
        }
        if (requestDTO.getTitle() != null) {
            course.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getDescription() != null) {
            course.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getPrice() != null) {
            course.setPrice(requestDTO.getPrice());
        }

        Course updatedCourse = courseRepository.save(course);
        return CourseDetailResponseDTO.from(updatedCourse);
    }

    // 코스 삭제 (Soft Delete)
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        // Soft delete: Course와 하위 Section, Lecture도 모두 soft delete
        course.softDelete();
        course.getSections().forEach(section -> {
            section.softDelete();
            // TODO: lecture.softDelete() 구현 후 cascade delete 활성화
            // section.getLectures().forEach(lecture -> lecture.softDelete());
        });

        courseRepository.save(course);
    }

    // 코스 상태 변경
    @Transactional
    public CourseDetailResponseDTO changeCourseStatus(Long id, CourseStatus newStatus) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new CourseNotFoundException(id));

        // 상태 전환 규칙 검증
        validateStatusTransition(course.getStatus(), newStatus);

        course.setStatus(newStatus);
        Course updatedCourse = courseRepository.save(course);
        return CourseDetailResponseDTO.from(updatedCourse);
    }

    // 코스 발행 (DRAFT/ARCHIVED -> PUBLISHED)
    @Transactional
    public CourseDetailResponseDTO publishCourse(Long id) {
        return changeCourseStatus(id, CourseStatus.PUBLISHED);
    }

    // 코스 아카이빙 (DRAFT/PUBLISHED -> ARCHIVED)
    @Transactional
    public CourseDetailResponseDTO archiveCourse(Long id) {
        return changeCourseStatus(id, CourseStatus.ARCHIVED);
    }

    // 상태 전환 규칙 검증
    private void validateStatusTransition(CourseStatus currentStatus, CourseStatus newStatus) {
        if (currentStatus == CourseStatus.DELETED) {
            throw new InvalidCourseStatusException("삭제된 코스의 상태는 변경할 수 없습니다.");
        }

        // DRAFT -> PUBLISHED, ARCHIVED 가능
        // PUBLISHED -> ARCHIVED 가능
        // ARCHIVED -> PUBLISHED 가능 (재개설)
        // DELETED -> 영구적 변경 불가
    }
}
