package com.example.projectlxp.service.course.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CourseCreateRequestDTO {

    @NotNull(message = "강사 ID는 필수입니다.")
    private Long instructorId;

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    @NotBlank(message = "코스 제목은 필수입니다.")
    private String title;

    private String description;

    @NotNull(message = "가격은 필수입니다.")
    @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다.")
    private BigDecimal price;

    // Constructors
    public CourseCreateRequestDTO() {
    }

    public CourseCreateRequestDTO(Long instructorId, Long categoryId, String title, String description, BigDecimal price) {
        this.instructorId = instructorId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters
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
}
