package com.example.projectlxp.service.lecture.dto;

import com.example.projectlxp.model.lecture.LectureType;
import jakarta.validation.constraints.Min;

public class LectureUpdateRequestDTO {

    private String title;
    private String description;
    private Integer order;
    private LectureType type;
    private String resourcePath;

    @Min(value = 0, message = "영상 길이는 0 이상이어야 합니다.")
    private Integer duration;

    private Boolean isPreviewable;

    // Constructors
    public LectureUpdateRequestDTO() {
    }

    public LectureUpdateRequestDTO(String title, String description, Integer order, LectureType type, String resourcePath, Integer duration, Boolean isPreviewable) {
        this.title = title;
        this.description = description;
        this.order = order;
        this.type = type;
        this.resourcePath = resourcePath;
        this.duration = duration;
        this.isPreviewable = isPreviewable;
    }

    // Getters and Setters
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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public LectureType getType() {
        return type;
    }

    public void setType(LectureType type) {
        this.type = type;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Boolean getIsPreviewable() {
        return isPreviewable;
    }

    public void setIsPreviewable(Boolean isPreviewable) {
        this.isPreviewable = isPreviewable;
    }
}
