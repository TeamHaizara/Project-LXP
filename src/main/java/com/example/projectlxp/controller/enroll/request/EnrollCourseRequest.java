package com.example.projectlxp.controller.enroll.request;

import com.example.projectlxp.model.payment.enums.PaymentMethod;
import com.example.projectlxp.service.enroll.dto.EnrollCourseServiceDto;
import jakarta.validation.constraints.NotNull;

public record EnrollCourseRequest(
    @NotNull(message = "강좌를 선택해 주세요.")
    Long courseId,
    @NotNull(message = "결제 방법을 선택해 주세요.")
    PaymentMethod paymentMethod
) {
    public EnrollCourseServiceDto toDto(Long userId) {
        return new EnrollCourseServiceDto(userId, courseId, paymentMethod);
    }
}
