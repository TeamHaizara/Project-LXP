package com.example.projectlxp.service.enroll.dto;

import com.example.projectlxp.model.enroll.EnrolledCourse;
import com.example.projectlxp.model.payment.enums.PaymentMethod;

import java.time.LocalDateTime;

public record EnrollCourseServiceDto(
    Long userId,
    Long courseId,
    PaymentMethod paymentMethod
) {

    public EnrolledCourse toEntity() {
        LocalDateTime enrolledAt = LocalDateTime.now();

        return EnrolledCourse.create(courseId, userId, enrolledAt);
    }
}
