package com.example.projectlxp.controller.lecture.response;

import com.example.projectlxp.model.lecture.Lecture;
import com.example.projectlxp.model.lecture.LectureType;

import java.time.LocalDateTime;

public class LectureResponse {

    private Long id;
    private Long sectionId;
    private String title;
    private String description;
    private Integer order;
    private LectureType type;
    private String resourcePath;
    private Integer duration;
    private Boolean previewable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public LectureResponse() {
    }

    public LectureResponse(Long id, Long sectionId, String title, String description, Integer order, LectureType type, String resourcePath, Integer duration, Boolean previewable, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sectionId = sectionId;
        this.title = title;
        this.description = description;
        this.order = order;
        this.type = type;
        this.resourcePath = resourcePath;
        this.duration = duration;
        this.previewable = previewable;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory method
    public static LectureResponse from(Lecture lecture) {
        return new LectureResponse(
                lecture.getId(),
                lecture.getSection().getId(),
                lecture.getTitle(),
                lecture.getDescription(),
                lecture.getSortOrder(),
                lecture.getType(),
                lecture.getResourcePath(),
                lecture.getDuration(),
                lecture.isPreviewable(),
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

    public Boolean getPreviewable() {
        return previewable;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
