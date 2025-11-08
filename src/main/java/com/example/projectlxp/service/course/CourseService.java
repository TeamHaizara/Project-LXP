package com.example.projectlxp.service.course;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.enroll.EnrolledCourseRepository;
import com.example.projectlxp.service.course.dto.CourseCreateRequest;
import com.example.projectlxp.service.course.dto.CourseDetailResponse;
import com.example.projectlxp.service.course.dto.CourseListResponse;
import com.example.projectlxp.service.course.dto.CourseUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrolledCourseRepository enrolledCourseRepository;

    public CourseService(CourseRepository courseRepository, EnrolledCourseRepository enrolledCourseRepository) {
        this.courseRepository = courseRepository;
        this.enrolledCourseRepository = enrolledCourseRepository;
    }

    // 코스 생성
    @Transactional
    public CourseDetailResponse createCourse(CourseCreateRequest requestDTO) {
        Course course = new Course(
                requestDTO.getInstructorId(),
                requestDTO.getCategoryId(),
                requestDTO.getTitle(),
                requestDTO.getDescription(),
                requestDTO.getPrice()
        );
        Course savedCourse = courseRepository.save(course);
        return CourseDetailResponse.from(savedCourse);
    }

    // 코스 조회 (ID)
    public CourseDetailResponse getCourseById(Long id) {
        Course course = courseRepository.findByIdWithSectionsAndLectures(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));
        return CourseDetailResponse.from(course);
    }

    // 여러 ID로 코스 목록 조회 (수강 중인 코스 목록용)
    public List<CourseListResponse> getCoursesByIds(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return List.of();
        }
        return courseRepository.findByIdsAndNotDeleted(courseIds).stream()
                .map(CourseListResponse::from)
                .collect(Collectors.toList());
    }

    // 모든 코스 조회
    public List<CourseListResponse> getAllCourses() {
        return courseRepository.findAllNotDeleted().stream()
                .map(CourseListResponse::from)
                .collect(Collectors.toList());
    }

    // 강사별 코스 조회
    public List<CourseListResponse> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorIdAndNotDeleted(instructorId).stream()
                .map(CourseListResponse::from)
                .collect(Collectors.toList());
    }

    // 카테고리별 코스 조회
    public List<CourseListResponse> getCoursesByCategory(Long categoryId) {
        return courseRepository.findByCategoryIdAndNotDeleted(categoryId).stream()
                .map(CourseListResponse::from)
                .collect(Collectors.toList());
    }

    // 상태별 코스 조회
    public List<CourseListResponse> getCoursesByStatus(String status) {
        CourseStatus courseStatus = CourseStatus.from(status);
        return courseRepository.findByStatusAndNotDeleted(courseStatus).stream()
                .map(CourseListResponse::from)
                .collect(Collectors.toList());
    }

    // 제목 검색
    public List<CourseListResponse> searchCoursesByTitle(String keyword) {
        return courseRepository.searchByTitleAndNotDeleted(keyword).stream()
                .map(CourseListResponse::from)
                .collect(Collectors.toList());
    }

    // 코스 수정
    @Transactional
    public CourseDetailResponse updateCourse(Long id, CourseUpdateRequest request) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));
        course.updateBasicInfo(
                request.getTitle(),
                request.getDescription(),
                request.getPrice(),
                request.getCategoryId()
        );
        return CourseDetailResponse.from(course);
    }

    // 코스 발행
    @Transactional
    public CourseDetailResponse publishCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));
        course.toPublished();
        return CourseDetailResponse.from(course);
    }

    // 코스 아카이빙
    @Transactional
    public CourseDetailResponse archiveCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));
        course.toArchived();
        return CourseDetailResponse.from(course);
    }

    // 코스 삭제
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ExceptionCode.COURSE_NOT_FOUND, id));
        int enrolledUserCount = enrolledCourseRepository.countByCourseId(id);
        course.toDeleted(enrolledUserCount); // validation + status transition first
        course.cascadeSoftDelete(); // actual cascade delete
    }

}
