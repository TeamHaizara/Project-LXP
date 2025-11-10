package com.example.projectlxp.controller.course.dto;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;

import java.time.LocalDateTime;

public class CourseResponse {

    private final Long id;
    private final Long instructorId;
    private final Long categoryId;
    private final String title;
    private final Integer price;
    private final CourseStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // Constructors
    public CourseResponse(Long id, Long instructorId, Long categoryId, String title, Integer price, CourseStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.instructorId = instructorId;
        this.categoryId = categoryId;
        this.title = title;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    // Getters
    public Long getId() {
        return id;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getPrice() {
        return price;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
