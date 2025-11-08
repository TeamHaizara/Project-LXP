package com.example.projectlxp.service.course;

import com.example.projectlxp.Exception.BusinessException;
import com.example.projectlxp.Exception.ExceptionCode;
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
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));
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
    public List<CourseListResponseDTO> getCoursesByStatus(String status) {
        CourseStatus courseStatus;
        try {
            courseStatus = CourseStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ExceptionCode.COURSE_NOT_FOUND, null);
        }
        return courseRepository.findByStatusAndNotDeleted(courseStatus).stream()
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
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));

        course.updateBasicInfo(
            requestDTO.getTitle(),
            requestDTO.getDescription(),
            requestDTO.getPrice(),
            requestDTO.getCategoryId()
        );

        Course updatedCourse = courseRepository.save(course);
        return CourseDetailResponseDTO.from(updatedCourse);
    }

    // 코스 삭제 (Soft Delete)
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));

        // Soft delete: Course와 하위 Section, Lecture도 모두 soft delete
        course.softDelete();
        course.getSections().forEach(section -> {
            section.softDelete();
            section.getLectures().forEach(lecture -> lecture.softDelete());
        });

        courseRepository.save(course);
    }

    // 코스 상태 변경
    @Transactional
    public CourseDetailResponseDTO changeCourseStatus(Long id, CourseStatus newStatus) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));

        course.changeStatus(newStatus);
        Course updatedCourse = courseRepository.save(course);
        return CourseDetailResponseDTO.from(updatedCourse);
    }

    // 코스 발행 (DRAFT/ARCHIVED -> PUBLISHED)
    @Transactional
    public CourseDetailResponseDTO publishCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));

        course.publish();
        Course updatedCourse = courseRepository.save(course);
        return CourseDetailResponseDTO.from(updatedCourse);
    }

    // 코스 아카이빙 (DRAFT/PUBLISHED -> ARCHIVED)
    @Transactional
    public CourseDetailResponseDTO archiveCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));

        course.archive();
        Course updatedCourse = courseRepository.save(course);
        return CourseDetailResponseDTO.from(updatedCourse);
    }
}
