package com.example.projectlxp.controller.enroll;

import com.example.projectlxp.controller.enroll.dto.EnrollCourseRequest;
import com.example.projectlxp.service.enroll.EnrolledCourseService;
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
    public void enroll(@RequestBody EnrollCourseRequest request, Long userId) {
        enrolledCourseService.enroll(request.toDto(userId));
    }
}
