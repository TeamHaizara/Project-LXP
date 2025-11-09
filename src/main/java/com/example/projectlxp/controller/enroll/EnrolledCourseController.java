package com.example.projectlxp.controller.enroll;

import com.example.projectlxp.controller.enroll.request.EnrollCourseRequest;
import com.example.projectlxp.controller.enroll.response.EnrolledCoursesResponse;
import com.example.projectlxp.service.enroll.EnrolledCourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnrolledCourseController {
    private final EnrolledCourseService enrolledCourseService;

    public EnrolledCourseController(EnrolledCourseService enrolledCourseService) {
        this.enrolledCourseService = enrolledCourseService;
    }

    @PostMapping("/api/learner/enroll")
    public ResponseEntity<Void> enroll(@RequestBody EnrollCourseRequest request, Long userId) {
        enrolledCourseService.enroll(request.toDto(userId));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/learner/enrolled-courses")
    public EnrolledCoursesResponse getEnrolledCourses(Long userId) {
        return EnrolledCoursesResponse.of(enrolledCourseService.getEnrolledCourses(userId));
    }
}
