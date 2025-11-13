package com.example.projectlxp.model.cart;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime deletedAt;

    protected Cart() {
    }

    private Cart(Long userId, Long courseId) {
        validateUserIdIsNull(userId);
        validateCourseIdIsNull(courseId);
        this.userId = userId;
        this.courseId = courseId;
    }

    public static Cart of(Long userId, Long courseId) {
        return new Cart(userId, courseId);
    }

    private void validateCourseIdIsNull(Long courseId) {
        Optional.ofNullable(courseId)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.NOT_NULL_FIELD_IS_NULL)
                        .withField("courseId")
                        .build());
    }

    private void validateUserIdIsNull(Long userId) {
        Optional.ofNullable(userId)
                .orElseThrow(() -> BusinessException.builder(ExceptionCode.NOT_NULL_FIELD_IS_NULL)
                        .withField("userId")
                        .build());
    }

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getCartUserId() {
        return userId;
    }

    public Long getCartCourseId() {
        return courseId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
}