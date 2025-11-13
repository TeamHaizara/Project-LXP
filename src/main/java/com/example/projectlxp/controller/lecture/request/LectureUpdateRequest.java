package com.example.projectlxp.controller.lecture.request;

import com.example.projectlxp.model.lecture.LectureType;
import jakarta.validation.constraints.Min;

public class LectureUpdateRequest {
    private Long lectureId;
    private String title;
    private String description;
    private Integer order;
    private LectureType type;
    private String resourcePath;

    @Min(value = 0, message = "영상 길이는 0 이상이어야 합니다.")
    private Integer duration;

    private Boolean previewable;

    // Constructors
    public LectureUpdateRequest() {
    }

    public LectureUpdateRequest(Long lectureId, String title, String description, Integer order, LectureType type, String resourcePath, Integer duration, Boolean previewable) {
        this.lectureId = lectureId;
        this.title = title;
        this.description = description;
        this.order = order;
        this.type = type;
        this.resourcePath = resourcePath;
        this.duration = duration;
        this.previewable = previewable;
    }

    // Getters and Setters
    public Long getLectureId() {
        return lectureId;
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

}
