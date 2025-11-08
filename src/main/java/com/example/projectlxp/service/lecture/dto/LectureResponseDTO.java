package com.example.projectlxp.service.lecture.dto;

import com.example.projectlxp.model.lecture.Lecture;
import com.example.projectlxp.model.lecture.LectureType;

import java.time.LocalDateTime;

public class LectureResponseDTO {

    private Long id;
    private Long sectionId;
    private String title;
    private String description;
    private Integer order;
    private LectureType type;
    private String resourcePath;
    private Integer duration;
    private Boolean isPreviewable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public LectureResponseDTO() {
    }

    public LectureResponseDTO(Long id, Long sectionId, String title, String description, Integer order, LectureType type, String resourcePath, Integer duration, Boolean isPreviewable, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sectionId = sectionId;
        this.title = title;
        this.description = description;
        this.order = order;
        this.type = type;
        this.resourcePath = resourcePath;
        this.duration = duration;
        this.isPreviewable = isPreviewable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory method
    public static LectureResponseDTO from(Lecture lecture) {
        return new LectureResponseDTO(
                lecture.getId(),
                lecture.getSection().getId(),
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getOrder(),
                lecture.getType(),
                lecture.getResourcePath(),
                lecture.getDuration(),
                lecture.getIsPreviewable(),
                lecture.getCreatedAt(),
                lecture.getUpdatedAt()
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Long getSectionId() {
        return sectionId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getOrder() {
        return order;
    }

    public LectureType getType() {
        return type;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public Integer getDuration() {
        return duration;
    }

    public Boolean getIsPreviewable() {
        return isPreviewable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
