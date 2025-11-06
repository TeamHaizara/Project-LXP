package com.example.projectlxp.service.course.dto;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CourseListResponseDTO {

    private Long id;
    private Long instructorId;
    private Long categoryId;
    private String title;
    private BigDecimal price;
    private CourseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public CourseListResponseDTO() {
    }

    public CourseListResponseDTO(Long id, Long instructorId, Long categoryId, String title, BigDecimal price, CourseStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
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
    public static CourseListResponseDTO from(Course course) {
        return new CourseListResponseDTO(
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Long instructorId) {
        this.instructorId = instructorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
