package com.example.projectlxp.model.payment;

import com.example.projectlxp.model.payment.enums.PaymentMethod;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long courseId;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private String paymentNumber;
    private BigDecimal paymentAmount;
    private LocalDateTime paymentAt;

    public Payment(
        Long courseId, Long userId, PaymentMethod paymentMethod,
        BigDecimal paymentAmount, LocalDateTime paymentAt
    ) {
        this.courseId = courseId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.paymentNumber = generatePaymentNumber();
        this.paymentAmount = paymentAmount;
        this.paymentAt = paymentAt;
    }

    private String generatePaymentNumber() {
        return "";
    }

    protected Payment() {}
}
