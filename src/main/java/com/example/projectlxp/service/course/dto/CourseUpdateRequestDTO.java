package com.example.projectlxp.service.course.dto;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class CourseUpdateRequestDTO {

    private Long categoryId;
    private String title;
    private String description;
    private Integer price;

    // Constructors
    public CourseUpdateRequestDTO() {
    }

    public CourseUpdateRequestDTO(Long categoryId, String title, String description, Integer price) {
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    // Getters
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
