package com.example.projectlxp.controller.course.response;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;

import java.time.LocalDateTime;

public record CourseResponse(
        Long id,
        Long instructorId,
        Long categoryId,
        String title,
        Integer price,
        CourseStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // Factory method
    public static CourseResponse from(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getInstructorId(),
                course.getCategoryId(),
                course.getTitle(),
                course.getPrice(),
                course.getStatus(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }
    
}
