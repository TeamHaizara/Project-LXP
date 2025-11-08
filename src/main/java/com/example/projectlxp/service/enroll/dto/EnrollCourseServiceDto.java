package com.example.projectlxp.service.enroll.dto;

import com.example.projectlxp.model.enroll.EnrolledCourse;

import java.time.LocalDateTime;

public record EnrollCourseServiceDto(
    Long userId,
    Long courseId
) {

    public EnrolledCourse toEntity() {
        LocalDateTime enrolledAt = LocalDateTime.now();

        return EnrolledCourse.create(courseId, userId, enrolledAt);
    }
}
