package com.example.projectlxp.controller.enroll;

import com.example.projectlxp.controller.enroll.request.EnrollCourseRequest;
import com.example.projectlxp.controller.enroll.response.EnrolledCoursesResponse;
import com.example.projectlxp.service.enroll.EnrolledCourseService;
import com.example.projectlxp.service.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<Void> enroll(
        @Valid @RequestBody EnrollCourseRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        enrolledCourseService.enroll(request.toDto(userDetails.getUserId()));

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/learner/enrolled-courses")
    public EnrolledCoursesResponse getEnrolledCourses(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return EnrolledCoursesResponse.of(enrolledCourseService.getEnrolledCourses(userDetails.getUserId()));
    }
}
