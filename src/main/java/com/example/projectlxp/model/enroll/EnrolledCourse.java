package com.example.projectlxp.model.enroll;

import com.example.projectlxp.exception.BusinessException;
import com.example.projectlxp.exception.ExceptionCode;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
public class EnrolledCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long courseId;
    private Long userId;
    private LocalDateTime enrolledAt;

    private EnrolledCourse(Long courseId, Long userId, LocalDateTime enrolledAt) {
        validateUserIdIsNull(userId);
        validateCourseIdIsNull(courseId);

        this.courseId = courseId;
        this.userId = userId;
        this.enrolledAt = enrolledAt;
    }

    public static EnrolledCourse create(Long courseId, Long userId, LocalDateTime enrolledAt) {
        return new EnrolledCourse(courseId, userId, enrolledAt);
    }

    private void validateCourseIdIsNull(Long courseId) {
        Optional.ofNullable(courseId)
            .orElseThrow(() ->
                new BusinessException(ExceptionCode.NOT_NULL_FIELD_IS_NULL, "courseId")
            );
    }

    private void validateUserIdIsNull(Long userId) {
        Optional.ofNullable(userId)
            .orElseThrow(() ->
                new BusinessException(ExceptionCode.NOT_NULL_FIELD_IS_NULL, "userId")
            );
    }

    protected EnrolledCourse() {}
}
