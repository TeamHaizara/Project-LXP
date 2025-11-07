package com.example.projectlxp.controller.course;

import com.example.projectlxp.model.course.CourseStatus;
import com.example.projectlxp.service.course.CourseService;
import com.example.projectlxp.service.course.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // 코스 생성
    @PostMapping("/create")
    public ResponseEntity<CourseDetailResponseDTO> createCourse(@Valid @RequestBody CourseCreateRequestDTO requestDTO) {
        CourseDetailResponseDTO response = courseService.createCourse(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 코스 조회 (ID)
    @GetMapping("/{course_id}")
    public ResponseEntity<CourseDetailResponseDTO> getCourse(@PathVariable("course_id") Long courseId) {
        CourseDetailResponseDTO response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(response);
    }

    // 여러 ID로 코스 조회 (내가 수강 중인 코스 목록)
    @PostMapping("/enrolled")
    public ResponseEntity<List<CourseListResponseDTO>> getCoursesByIds(@RequestBody List<Long> courseIds) {
        List<CourseListResponseDTO> response = courseService.getCoursesByIds(courseIds);
        return ResponseEntity.ok(response);
    }

    // 모든 코스 조회
    @GetMapping
    public ResponseEntity<List<CourseListResponseDTO>> getAllCourses() {
        List<CourseListResponseDTO> response = courseService.getAllCourses();
        return ResponseEntity.ok(response);
    }

    // 강사별 코스 조회
    @GetMapping("/instructors/{instructor_id}")
    public ResponseEntity<List<CourseListResponseDTO>> getCoursesByInstructor(
            @PathVariable("instructor_id") Long instructorId
    ) {
        List<CourseListResponseDTO> response = courseService.getCoursesByInstructor(instructorId);
        return ResponseEntity.ok(response);
    }

    // 카테고리별 코스 조회
    @GetMapping("/categories/{category_id}")
    public ResponseEntity<List<CourseListResponseDTO>> getCoursesByCategory(
            @PathVariable("category_id") Long categoryId
    ) {
        List<CourseListResponseDTO> response = courseService.getCoursesByCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    // 상태별 코스 조회
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CourseListResponseDTO>> getCoursesByStatus(@PathVariable CourseStatus status) {
        List<CourseListResponseDTO> response = courseService.getCoursesByStatus(status);
        return ResponseEntity.ok(response);
    }

    // 코스 검색 (제목)
    @GetMapping("/search")
    public ResponseEntity<List<CourseListResponseDTO>> searchCourses(@RequestParam String keyword) {
        List<CourseListResponseDTO> response = courseService.searchCoursesByTitle(keyword);
        return ResponseEntity.ok(response);
    }

    // 코스 수정
    @PutMapping("/{course_id}")
    public ResponseEntity<CourseDetailResponseDTO> updateCourse(
            @PathVariable("course_id") Long courseId,
            @Valid @RequestBody CourseUpdateRequestDTO requestDTO
    ) {
        CourseDetailResponseDTO response = courseService.updateCourse(courseId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 코스 삭제
    @DeleteMapping("/{course_id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable("course_id") Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    // 코스 발행
    @PostMapping("/{course_id}/publish")
    public ResponseEntity<CourseDetailResponseDTO> publishCourse(@PathVariable("course_id") Long courseId) {
        CourseDetailResponseDTO response = courseService.publishCourse(courseId);
        return ResponseEntity.ok(response);
    }

    // 코스 아카이빙
    @PostMapping("/{course_id}/archive")
    public ResponseEntity<CourseDetailResponseDTO> archiveCourse(@PathVariable("course_id") Long courseId) {
        CourseDetailResponseDTO response = courseService.archiveCourse(courseId);
        return ResponseEntity.ok(response);
    }
}
