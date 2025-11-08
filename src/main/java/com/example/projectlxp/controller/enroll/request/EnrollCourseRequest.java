package com.example.projectlxp.controller.enroll.request;

import com.example.projectlxp.service.enroll.dto.EnrollCourseServiceDto;

public record EnrollCourseRequest(
    Long courseId
) {
    public EnrollCourseServiceDto toDto(Long userId) {
        return new EnrollCourseServiceDto(userId, courseId);
    }
}
