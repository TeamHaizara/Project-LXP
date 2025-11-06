package com.example.projectlxp.service.course.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class CourseUpdateRequestDTO {

    private Long categoryId;
    private String title;
    private String description;

    @DecimalMin(value = "0.0", message = "가격은 0 이상이어야 합니다.")
    private BigDecimal price;

    // Constructors
    public CourseUpdateRequestDTO() {
    }

    public CourseUpdateRequestDTO(Long categoryId, String title, String description, BigDecimal price) {
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    // Getters and Setters
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
