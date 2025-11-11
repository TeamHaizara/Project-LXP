package com.example.projectlxp.controller.course.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CourseUpdateRequest {

    private Long categoryId;
    
    @NotBlank(message = "제목은 빈 값일 수 없습니다")
    @Size(max = 255, message = "제목은 255자를 초과할 수 없습니다") // TODO: VARCHAR 기본 SIZE, 초과시 에러 발생
    private String title;
    
    @Size(max = 5000, message = "설명은 5000자를 초과할 수 없습니다") // TODO: 비즈니스 요구사항 확정 후 조정 필요
    private String description;
    
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
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
