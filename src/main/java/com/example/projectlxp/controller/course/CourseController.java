package com.example.projectlxp.controller.course;

import com.example.projectlxp.controller.course.request.CourseCreateRequest;
import com.example.projectlxp.controller.course.request.CourseUpdateRequest;
import com.example.projectlxp.controller.course.response.CourseDetailResponse;
import com.example.projectlxp.controller.course.response.CourseListResponse;
import com.example.projectlxp.service.course.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // 코스 생성
    @PostMapping("/create")
    public ResponseEntity<CourseDetailResponse> createCourse(@Valid @RequestBody CourseCreateRequest requestDTO) {
        CourseDetailResponse response = courseService.createCourse(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 코스 조회 (ID)
    @GetMapping("/{course_id}")
    public ResponseEntity<CourseDetailResponse> getCourse(@PathVariable("course_id") Long courseId) {
        CourseDetailResponse response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(response);
    }

    // 모든 코스 조회
    @GetMapping
    public ResponseEntity<CourseListResponse> getAllCourses() {
        CourseListResponse response = courseService.getAllCourses();
        return ResponseEntity.ok(response);
    }

    // 강사별 코스 조회
    @GetMapping("/instructors/{instructor_id}")
    public ResponseEntity<CourseListResponse> getCoursesByInstructor(
            @PathVariable("instructor_id") Long instructorId
    ) {
        CourseListResponse response = courseService.getCoursesByInstructor(instructorId);
        return ResponseEntity.ok(response);
    }

    // 카테고리별 코스 조회
    @GetMapping("/categories/{category_id}")
    public ResponseEntity<CourseListResponse> getCoursesByCategory(
            @PathVariable("category_id") Long categoryId
    ) {
        CourseListResponse response = courseService.getCoursesByCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    // 상태별 코스 조회
    @GetMapping("/status")
    public ResponseEntity<CourseListResponse> getCoursesByStatus(@RequestParam String status) {
        CourseListResponse response = courseService.getCoursesByStatus(status);
        return ResponseEntity.ok(response);
    }

    // 코스 검색 (제목)
    @GetMapping("/search")
    public ResponseEntity<CourseListResponse> searchCourses(@RequestParam String keyword) {
        CourseListResponse response = courseService.searchCoursesByTitle(keyword);
        return ResponseEntity.ok(response);
    }

    // 코스 수정
    @PutMapping("/{course_id}")
    public ResponseEntity<CourseDetailResponse> updateCourse(
            @PathVariable("course_id") Long courseId,
            @Valid @RequestBody CourseUpdateRequest requestDTO
    ) {
        CourseDetailResponse response = courseService.updateCourse(courseId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 코스 발행
    @PostMapping("/{course_id}/publish")
    public ResponseEntity<CourseDetailResponse> publishCourse(@PathVariable("course_id") Long courseId) {
        CourseDetailResponse response = courseService.publishCourse(courseId);
        return ResponseEntity.ok(response);
    }

    // 코스 아카이빙
    @PostMapping("/{course_id}/archive")
    public ResponseEntity<CourseDetailResponse> archiveCourse(@PathVariable("course_id") Long courseId) {
        CourseDetailResponse response = courseService.archiveCourse(courseId);
        return ResponseEntity.ok(response);
    }

    // 코스 삭제
    @DeleteMapping("/{course_id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("course_id") Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

}
