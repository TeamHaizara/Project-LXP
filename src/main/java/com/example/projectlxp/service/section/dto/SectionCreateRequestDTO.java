package com.example.projectlxp.service.section.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SectionCreateRequestDTO {

    @NotNull(message = "코스 ID는 필수입니다.")
    private Long courseId;

    @NotBlank(message = "섹션 제목은 필수입니다.")
    private String title;

    private Integer order;

    // Constructors
    public SectionCreateRequestDTO() {
    }

    public SectionCreateRequestDTO(Long courseId, String title, Integer order) {
        this.courseId = courseId;
        this.title = title;
        this.order = order;
    }

    // Getters and Setters
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
}
