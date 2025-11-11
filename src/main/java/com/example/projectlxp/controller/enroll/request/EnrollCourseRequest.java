package com.example.projectlxp.controller.enroll.request;

import com.example.projectlxp.model.payment.enums.PaymentMethod;
import com.example.projectlxp.service.enroll.dto.EnrollCourseServiceDto;

public record EnrollCourseRequest(
    Long courseId,
    PaymentMethod paymentMethod
) {
    public EnrollCourseServiceDto toDto(Long userId) {
        return new EnrollCourseServiceDto(userId, courseId, paymentMethod);
    }
}
