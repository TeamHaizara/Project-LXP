package com.example.projectlxp.service.course.dto;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;
import com.example.projectlxp.service.section.dto.SectionResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CourseDetailResponseDTO {

    private Long id;
    private Long instructorId;
    private Long categoryId;
    private String title;
    private String description;
    private BigDecimal price;
    private CourseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SectionResponseDTO> sections;

    // Constructors
    public CourseDetailResponseDTO() {
    }

    public CourseDetailResponseDTO(Long id, Long instructorId, Long categoryId, String title, String description, BigDecimal price, CourseStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, List<SectionResponseDTO> sections) {
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
    public static CourseDetailResponseDTO from(Course course) {
        List<SectionResponseDTO> sections = course.getSections().stream()
                .filter(section -> !section.isDeleted())
                .map(SectionResponseDTO::from)
                .collect(Collectors.toList());

        return new CourseDetailResponseDTO(
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<SectionResponseDTO> getSections() {
        return sections;
    }

    public void setSections(List<SectionResponseDTO> sections) {
        this.sections = sections;
    }
}
