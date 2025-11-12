package com.example.projectlxp.service.course;

import com.example.projectlxp.controller.course.request.CourseCreateRequest;
import com.example.projectlxp.controller.course.request.CourseUpdateRequest;
import com.example.projectlxp.controller.course.response.CourseDetailResponse;
import com.example.projectlxp.controller.course.response.CourseListResponse;
import com.example.projectlxp.controller.course.response.CourseResponse;
import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;
import com.example.projectlxp.repository.course.CourseRepository;
import com.example.projectlxp.repository.enroll.EnrolledCourseRepository;
import com.example.projectlxp.service.course.dto.CourseSearchCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // Step 1: Fetch course with sections
        Course course = courseRepository.findByIdWithSections(id)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.COURSE_NOT_FOUND)
                        .withId(id)
                        .build());
        // Step 2: Fetch lectures for all sections (solves MultipleBagFetchException)
        // 따로 값을 저장하지 않아도 transactional context 안에서 매핑됨
        courseRepository.findSectionsWithLecturesByCourseId(id);

        return CourseDetailResponse.from(course);
    }

    // 모든 코스 조회
    public CourseListResponse getAllCourses() {
        return CourseListResponse.from(
                courseRepository.findAllPublished().stream()
                        .map(CourseResponse::from)
                        .collect(Collectors.toList())
        );
    }

    // 검색 조건으로 코스 조회 (동적 필터링)
    public CourseListResponse searchCourses(CourseSearchCriteria criteria) {
        List<Course> courses = criteria.hasAnyFilter()
                ? courseRepository.searchByCriteria(criteria)
                : courseRepository.findAllPublished();

        return CourseListResponse.from(
                courses.stream()
                        .map(CourseResponse::from)
                        .collect(Collectors.toList())
        );
    }

    // 강사별 코스 조회 (조회용, published only)
    public CourseListResponse getCoursesByInstructor(Long instructorId) {
        return CourseListResponse.from(
                courseRepository.findByInstructorIdAndPublished(instructorId).stream()
                        .map(CourseResponse::from)
                        .collect(Collectors.toList())
        );
    }

    // 강사별 코스 조회 (관리용, all status)
    public CourseListResponse getCoursesByInstructorManage(Long instructorId) {
        return CourseListResponse.from(
                courseRepository.findByInstructorIdAndNotDeleted(instructorId).stream()
                        .map(CourseResponse::from)
                        .collect(Collectors.toList())
        );
    }

    // 카테고리별 코스 조회
    public CourseListResponse getCoursesByCategory(Long categoryId) {
        return CourseListResponse.from(
                courseRepository.findByCategoryIdAndPublished(categoryId).stream()
                        .map(CourseResponse::from)
                        .collect(Collectors.toList())
        );
    }

    // 상태별 코스 조회
    public CourseListResponse getCoursesByStatus(String status) {
        CourseStatus courseStatus = CourseStatus.from(status);
        return CourseListResponse.from(
                courseRepository.findByStatus(courseStatus).stream()
                        .map(CourseResponse::from)
                        .collect(Collectors.toList())
        );
    }

    // 제목 검색
    public CourseListResponse searchCoursesByTitle(String keyword) {
        return CourseListResponse.from(
                courseRepository.searchByTitleAndPublished(keyword).stream()
                        .map(CourseResponse::from)
                        .collect(Collectors.toList())
        );
    }

    // 코스 수정
    @Transactional
    public CourseDetailResponse updateCourse(Long id, CourseUpdateRequest request) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.COURSE_NOT_FOUND)
                        .withId(id)
                        .build());
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
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.COURSE_NOT_FOUND)
                        .withId(id)
                        .build());
        course.publish();
        return CourseDetailResponse.from(course);
    }

    // 코스 아카이빙
    @Transactional
    public CourseDetailResponse archiveCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.COURSE_NOT_FOUND)
                        .withId(id)
                        .build());
        course.archive();
        return CourseDetailResponse.from(course);
    }

    // 코스 삭제
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.COURSE_NOT_FOUND)
                        .withId(id)
                        .build());
        int enrolledUserCount = enrolledCourseRepository.countByCourseId(id);
        course.delete(enrolledUserCount); // validation + status transition + cascade delete
    }

}
