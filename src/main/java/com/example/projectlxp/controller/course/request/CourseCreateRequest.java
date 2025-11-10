package com.example.projectlxp.controller.course.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CourseCreateRequest {

    @NotNull(message = "강사 ID는 필수입니다.")
    private Long instructorId;

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    @NotBlank(message = "코스 제목은 필수입니다.")
    private String title;

    private String description;

    @NotNull(message = "가격은 필수입니다.")
    private Integer price;

    // Constructors
    public CourseCreateRequest(Long instructorId, Long categoryId, String title, String description, Integer price) {
        this.instructorId = instructorId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    // Getters
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

}
