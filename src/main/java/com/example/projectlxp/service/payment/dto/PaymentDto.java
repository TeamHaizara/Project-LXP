package com.example.projectlxp.service.payment.dto;

import com.example.projectlxp.model.payment.Payment;
import com.example.projectlxp.model.payment.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDto(
    Long userId,
    Long courseId,
    BigDecimal price,
    PaymentMethod paymentMethod
) {
    public Payment toEntity() {
        LocalDateTime paymentAt = LocalDateTime.now();

        return new Payment(courseId, userId, paymentMethod, price, paymentAt);
    }
}
