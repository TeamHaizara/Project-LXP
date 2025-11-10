package com.example.projectlxp.controller.course.request;

public class CourseUpdateRequest {

    private Long categoryId;
    private String title;
    private String description;
    private Integer price;

    // Constructors
    public CourseUpdateRequest() {
    }

    public CourseUpdateRequest(Long categoryId, String title, String description, Integer price) {
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
