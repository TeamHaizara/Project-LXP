package com.example.projectlxp.controller.course;

import com.example.projectlxp.controller.course.request.CourseCreateRequest;
import com.example.projectlxp.controller.course.request.CourseUpdateRequest;
import com.example.projectlxp.controller.course.response.CourseDetailResponse;
import com.example.projectlxp.controller.course.response.CourseListResponse;
import com.example.projectlxp.controller.lecture.request.LectureCreateRequest;
import com.example.projectlxp.controller.lecture.request.LectureUpdateRequest;
import com.example.projectlxp.controller.lecture.response.LectureResponse;
import com.example.projectlxp.controller.section.request.SectionCreateRequest;
import com.example.projectlxp.controller.section.request.SectionUpdateRequest;
import com.example.projectlxp.controller.section.response.SectionResponse;
import com.example.projectlxp.service.course.CourseService;
import com.example.projectlxp.service.course.dto.CourseSearchCriteria;
import com.example.projectlxp.service.lecture.LectureService;
import com.example.projectlxp.service.section.SectionService;
import com.example.projectlxp.service.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        CourseSearchCriteria criteria = new CourseSearchCriteria(instructorId, categoryId, keyword);
        CourseListResponse response = courseService.searchCourses(criteria);
        return ResponseEntity.ok(response);
    }

    // 코스 상세 조회 (섹션, 렉처 트리 포함)
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseDetailResponse> getCourse(
            @PathVariable Long courseId
    ) {
        CourseDetailResponse response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(response);
    }


    // ========== Course Management APIs ==========

    // 코스 생성
    @PostMapping("/instructor/courses")
    public ResponseEntity<CourseDetailResponse> createCourse(
            @Valid @RequestBody CourseCreateRequest requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CourseDetailResponse response = courseService.createCourse(requestDTO, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 코스 목록 조회 Instructor 관리용
    @GetMapping("/instructor/courses")
    public ResponseEntity<CourseListResponse> getInstructorCourses(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CourseListResponse response = courseService.getInstructorCourses(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    // 코스 수정
    @PutMapping("/instructor/courses/{courseId}")
    public ResponseEntity<CourseDetailResponse> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseUpdateRequest requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CourseDetailResponse response = courseService.updateCourse(courseId, requestDTO, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    // 코스 발행
    @PostMapping("/instructor/courses/{courseId}/publish")
    public ResponseEntity<CourseDetailResponse> publishCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CourseDetailResponse response = courseService.publishCourse(courseId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    // 코스 아카이빙
    @PostMapping("/instructor/courses/{courseId}/archive")
    public ResponseEntity<CourseDetailResponse> archiveCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CourseDetailResponse response = courseService.archiveCourse(courseId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    // 코스 삭제
    @DeleteMapping("/instructor/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        courseService.deleteCourse(courseId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    // ========== Section Management APIs ==========

    // 섹션 생성
    @PostMapping("/instructor/courses/{courseId}/sections")
    public ResponseEntity<SectionResponse> createSection(
            @PathVariable Long courseId,
            @Valid @RequestBody SectionCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        SectionResponse response = sectionService.createSection(request.toDto(courseId), userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 섹션 수정
    @PutMapping("/instructor/courses/{courseId}/sections/{sectionId}")
    public ResponseEntity<SectionResponse> updateSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @Valid @RequestBody SectionUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        SectionResponse response = sectionService.updateSection(courseId, sectionId, request.toDto(), userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    // 섹션 삭제
    @DeleteMapping("/instructor/courses/{courseId}/sections/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        sectionService.deleteSection(courseId, sectionId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 섹션 순서 변경
    @PutMapping("/instructor/courses/{courseId}/sections/reorder")
    public ResponseEntity<Void> reorderSections(
            @PathVariable Long courseId,
            @RequestBody List<Long> sectionIds,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        sectionService.reorderSections(courseId, sectionIds, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    // ========== Lecture Management APIs ==========

    // 렉처 생성
    @PostMapping("/instructor/courses/{courseId}/sections/{sectionId}/lectures")
    public ResponseEntity<LectureResponse> createLecture(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @Valid @RequestBody LectureCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        LectureResponse response = lectureService.createLecture(courseId, sectionId, request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 렉처 수정
    @PutMapping("/instructor/courses/{courseId}/sections/{sectionId}/lectures/{lectureId}")
    public ResponseEntity<LectureResponse> updateLecture(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lectureId,
            @Valid @RequestBody LectureUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        LectureResponse response = lectureService.updateLecture(courseId, sectionId, lectureId, request, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    // 렉처 삭제
    @DeleteMapping("/instructor/courses/{courseId}/sections/{sectionId}/lectures/{lectureId}")
    public ResponseEntity<Void> deleteLecture(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @PathVariable Long lectureId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        lectureService.deleteLecture(courseId, sectionId, lectureId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    // 섹션 내 렉처 순서 변경
    @PutMapping("/instructor/courses/{courseId}/sections/{sectionId}/lectures/reorder")
    public ResponseEntity<Void> reorderLectures(
            @PathVariable Long courseId,
            @PathVariable Long sectionId,
            @RequestBody List<Long> lectureIds,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        lectureService.reorderLectures(courseId, sectionId, lectureIds, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

}
