package com.example.projectlxp.controller.course.dto;

import java.util.List;

public class CourseListResponse {

    private final List<CourseResponse> content;

    public CourseListResponse(List<CourseResponse> course) {
        this.content = course;
    }

    public List<CourseResponse> getContent() {
        return List.copyOf(content);
    }

    // Factory method
    public static CourseListResponse from(List<CourseResponse> courses) {
        return new CourseListResponse(courses);
    }

}
