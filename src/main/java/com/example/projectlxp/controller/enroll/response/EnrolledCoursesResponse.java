package com.example.projectlxp.controller.enroll.response;

import com.example.projectlxp.model.course.Course;

import java.util.List;

public record EnrolledCoursesResponse(
    List<EnrolledCourseResponse> content
) {
    private record EnrolledCourseResponse(
        Long courseId,
        String title
    ) {

    }

    public static EnrolledCoursesResponse of(List<Course> courses) {
        return new EnrolledCoursesResponse(
            courses.stream().map(course ->
                new EnrolledCourseResponse(course.getId(), course.getTitle())
            ).toList()
        );
    }
}
