package com.example.projectlxp.controller.course;

import com.example.projectlxp.controller.course.request.CourseCreateRequest;
import com.example.projectlxp.controller.course.request.CourseUpdateRequest;
import com.example.projectlxp.controller.course.response.CourseDetailResponse;
import com.example.projectlxp.controller.course.response.CourseListResponse;
import com.example.projectlxp.controller.lecture.request.LectureCreateRequest;
import com.example.projectlxp.controller.lecture.request.LectureUpdateRequest;
import com.example.projectlxp.controller.lecture.response.LectureListResponse;
import com.example.projectlxp.controller.lecture.response.LectureResponse;
import com.example.projectlxp.controller.section.request.SectionCreateRequest;
import com.example.projectlxp.controller.section.request.SectionUpdateRequest;
import com.example.projectlxp.controller.section.response.SectionResponse;
import com.example.projectlxp.service.course.CourseService;
import com.example.projectlxp.service.lecture.LectureService;
import com.example.projectlxp.service.section.SectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseController {

    private final CourseService courseService;
    private final SectionService sectionService;
    private final LectureService lectureService;

    public CourseController(
            CourseService courseService,
            SectionService sectionService,
            LectureService lectureService
    ) {
        this.courseService = courseService;
        this.sectionService = sectionService;
        this.lectureService = lectureService;
    }

    // 코스 목록 조회 (쿼리 파라미터로 필터링)
    @GetMapping("/courses")
    public ResponseEntity<CourseListResponse> getCourses(
            @RequestParam(required = false) Long instructorId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword
    ) {
        CourseListResponse response;
        if (instructorId != null) {
            response = courseService.getCoursesByInstructor(instructorId);
        } else if (categoryId != null) {
            response = courseService.getCoursesByCategory(categoryId);
        } else if (keyword != null) {
            response = courseService.searchCoursesByTitle(keyword);
        } else {
            response = courseService.getAllCourses();
        }
        return ResponseEntity.ok(response);
    }

    // 코스 상세 조회 (섹션, 렉처 트리 포함)
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourse(@PathVariable Long courseId) {
        CourseDetailResponse response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(response);
    }


    // ========== Course Management APIs ==========

    // 코스 생성
    @PostMapping("/instructor/courses")
    public ResponseEntity<CourseDetailResponse> createCourse(@Valid @RequestBody CourseCreateRequest requestDTO) {
        CourseDetailResponse response = courseService.createCourse(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 코스 목록 조회 Instructor 관리용
    @GetMapping("/instructor/courses")
    public ResponseEntity<CourseListResponse> getCoursesManageable(
            @RequestParam(required = false) Long instructorId
    ) {
        CourseListResponse response = courseService.getCoursesByInstructorManage(instructorId);
        return ResponseEntity.ok(response);
    }

    // 코스 수정
    @PutMapping("/instructor/courses/{courseId}")
    public ResponseEntity<CourseDetailResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseUpdateRequest requestDTO
    ) {
        CourseDetailResponse response = courseService.updateCourse(courseId, requestDTO);
        return ResponseEntity.ok(response);
    }

    // 코스 발행
    @PostMapping("/instructor/courses/{courseId}/publish")
    public ResponseEntity<CourseDetailResponse> publishCourse(@PathVariable Long courseId) {
        CourseDetailResponse response = courseService.publishCourse(courseId);
        return ResponseEntity.ok(response);
    }

    // 코스 아카이빙
    @PostMapping("/instructor/courses/{courseId}/archive")
    public ResponseEntity<CourseDetailResponse> archiveCourse(@PathVariable Long courseId) {
        CourseDetailResponse response = courseService.archiveCourse(courseId);
        return ResponseEntity.ok(response);
    }

    // 코스 삭제
    @DeleteMapping("/instructor/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    // ========== Section Management APIs ==========

    // 섹션 생성
    @PostMapping("/instructor/courses/{courseId}/sections")
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long courseId,
            @Valid @RequestBody SectionCreateRequest request
    ) {
        SectionResponse response = sectionService.createSection(request.toDto(courseId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 섹션 수정
    @PutMapping("/instructor/courses/{courseId}/sections/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @Valid @RequestBody SectionUpdateRequest request
    ) {
        SectionResponse response = sectionService.updateSection(courseId, sectionId, request.toDto());
        return ResponseEntity.ok(response);
    }

    // 섹션 삭제
    @DeleteMapping("/instructor/courses/{courseId}/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId
    ) {
        sectionService.deleteSection(courseId, sectionId);
        return ResponseEntity.noContent().build();
    }

    // 섹션 순서 변경
    @PutMapping("/instructor/courses/{courseId}/sections/reorder")
    public ResponseEntity<Void> reorderSections(
            @PathVariable Long courseId,
            @RequestBody List<Long> sectionIds
    ) {
        sectionService.reorderSections(courseId, sectionIds);
        return ResponseEntity.noContent().build();
    }

    // ========== Lecture Management APIs ==========

    // 렉처 생성
    @PostMapping("/instructor/courses/{courseId}/sections/{sectionId}/lectures")
    public ResponseEntity<LectureResponse> createLecture(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @Valid @RequestBody LectureCreateRequest request
    ) {
        LectureResponse response = lectureService.createLecture(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 렉처 수정
    @PutMapping("/instructor/courses/{courseId}/sections/{sectionId}/lectures/{lectureId}")
    public ResponseEntity<LectureResponse> updateLecture(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lectureId,
            @Valid @RequestBody LectureUpdateRequest request
    ) {
        LectureResponse response = lectureService.updateLecture(sectionId, lectureId, request);
        return ResponseEntity.ok(response);
    }

    // 렉처 삭제
    @DeleteMapping("/instructor/courses/{courseId}/sections/{sectionId}/lectures/{lectureId}")
    public ResponseEntity<Void> deleteLecture(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lectureId
    ) {
        lectureService.deleteLecture(sectionId, lectureId);
        return ResponseEntity.noContent().build();
    }

    // 섹션 내 렉처 순서 변경
    @PutMapping("/instructor/courses/{courseId}/sections/{sectionId}/lectures/reorder")
    public ResponseEntity<Void> reorderLectures(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestBody List<Long> lectureIds
    ) {
        lectureService.reorderLectures(sectionId, lectureIds);
        return ResponseEntity.noContent().build();
    }

    // 섹션의 렉처 목록 조회
    @GetMapping("/instructor/courses/{courseId}/sections/{sectionId}/lectures")
    public ResponseEntity<LectureListResponse> getLecturesBySection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId
    ) {
        LectureListResponse response = lectureService.getLecturesBySection(sectionId);
        return ResponseEntity.ok(response);
    }

}
