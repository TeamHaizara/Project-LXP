package com.example.projectlxp.service.section.dto;

import com.example.projectlxp.model.section.Section;

import java.time.LocalDateTime;

public class SectionResponseDTO {

    private Long id;
    private Long courseId;
    private String title;
    private Integer order;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public SectionResponseDTO() {
    }

    public SectionResponseDTO(Long id, Long courseId, String title, Integer order, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.order = order;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory method
    public static SectionResponseDTO from(Section section) {
        return new SectionResponseDTO(
                section.getId(),
                section.getCourse().getId(),
                section.getTitle(),
                section.getOrder(),
                section.getCreatedAt(),
                section.getUpdatedAt()
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
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
