package com.example.projectlxp.service.course.dto;

public class CourseSearchCriteria {
    private final Long instructorId;
    private final Long categoryId;
    private final String keyword;

    public CourseSearchCriteria(Long instructorId, Long categoryId, String keyword) {
        this.instructorId = instructorId;
        this.categoryId = categoryId;
        this.keyword = keyword;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean hasAnyFilter() {
        return instructorId != null || categoryId != null || keyword != null;
    }
}
