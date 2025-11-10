package com.example.projectlxp.controller.course.response;

import java.util.List;

public record CourseListResponse(List<CourseResponse> content) {

    // Factory method
    public static CourseListResponse from(List<CourseResponse> courses) {
        return new CourseListResponse(courses);
    }

}
