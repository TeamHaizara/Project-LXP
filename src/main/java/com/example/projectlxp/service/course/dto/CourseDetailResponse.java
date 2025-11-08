package com.example.projectlxp.service.course.dto;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;
import com.example.projectlxp.service.section.dto.SectionResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CourseDetailResponse {

    private Long id;
    private Long instructorId;
    private Long categoryId;
    private String title;
    private String description;
    private Integer price;
    private CourseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SectionResponseDTO> sections;

    // Constructor
    public CourseDetailResponse(Long id, Long instructorId, Long categoryId, String title, String description, Integer price, CourseStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, List<SectionResponseDTO> sections) {
        this.id = id;
        this.instructorId = instructorId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sections = sections;
    }

    // Factory method
    public static CourseDetailResponse from(Course course) {
        List<SectionResponseDTO> sections = course.getSections().stream()
                .filter(section -> !section.isDeleted())
                .map(SectionResponseDTO::from)
                .collect(Collectors.toList());

        return new CourseDetailResponse(
                course.getId(),
                course.getInstructorId(),
                course.getCategoryId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.getStatus(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                sections
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

    public String getDescription() {
        return description;
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

    public List<SectionResponseDTO> getSections() {
        return sections;
    }
}
