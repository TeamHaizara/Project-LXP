package com.example.projectlxp.service.lecture.dto;

import com.example.projectlxp.model.lecture.LectureType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LectureCreateRequestDTO {

    @NotNull(message = "섹션 ID는 필수입니다.")
    private Long sectionId;

    @NotBlank(message = "렉처 제목은 필수입니다.")
    private String title;

    private String description;

    private Integer order;

    @NotNull(message = "렉처 타입은 필수입니다.")
    private LectureType type;

    @NotBlank(message = "강의 자료에 해당하는 Url이 필요합니다.")
    private String resourcePath;

    @Min(value = 0, message = "영상 길이는 0 이상이어야 합니다.")
    private Integer duration;

    private Boolean isPreviewable = false;

    // Constructors
    public LectureCreateRequestDTO() {
    }

    public LectureCreateRequestDTO(Long sectionId, String title, String description, Integer order, LectureType type, String resourcePath, Integer duration, Boolean isPreviewable) {
        this.sectionId = sectionId;
        this.title = title;
        this.description = description;
        this.order = order;
        this.type = type;
        this.resourcePath = resourcePath;
        this.duration = duration;
        this.isPreviewable = isPreviewable;
    }

    // Getters and Setters
    public Long getSectionId() {
        return sectionId;
    }

    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
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
