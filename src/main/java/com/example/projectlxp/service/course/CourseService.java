package com.example.projectlxp.service.course;

import com.example.projectlxp.controller.BusinessException;
import com.example.projectlxp.controller.ExceptionCode;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.service.course.dto.CourseCreateRequestDTO;
import com.example.projectlxp.service.course.dto.CourseDetailResponseDTO;
import com.example.projectlxp.service.course.dto.CourseListResponseDTO;
import com.example.projectlxp.service.course.dto.CourseUpdateRequestDTO;
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
        CourseStatus courseStatus = CourseStatus.from(status);
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
        return CourseDetailResponseDTO.from(course);
    }

    // 코스 발행
    @Transactional
    public CourseDetailResponseDTO publishCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));
        course.toPublished();
        return CourseDetailResponseDTO.from(course);
    }

    // 코스 아카이빙
    @Transactional
    public CourseDetailResponseDTO archiveCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));
        course.toArchived();
        return CourseDetailResponseDTO.from(course);
    }

    // 코스 삭제
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));
        // TODO: Enrollment 엔티티 구현 후 enrolled user 수 조회
        int enrolledUserCount = 0; // enrollmentRepository.countByCourseId(id);
        course.cascadeSoftDelete();
        course.toDeleted(enrolledUserCount);
    }

}
