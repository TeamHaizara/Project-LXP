package com.example.projectlxp.controller.course.response;

import com.example.projectlxp.controller.section.response.SectionResponse;
import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.course.CourseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record CourseDetailResponse(
        Long id,
        Long instructorId,
        Long categoryId,
        String title,
        String description,
        Integer price,
        CourseStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<SectionResponse> sections
) {

    // Factory method
    public static CourseDetailResponse from(Course course) {
        List<SectionResponse> sections = course.getSections().stream()
                .filter(section -> !section.isDeleted())
                .map(SectionResponse::from)
                .collect(Collectors.toList());

        return new CourseDetailResponse(
                course.getId(),
                course.getInstructorId(),
                course.getCategoryId(),
                course.getTitle(),
                course.getDescription(),
                course.getPrice(),
                course.getStatus(),
                course.getCreatedAt(),
                course.getUpdatedAt(),
                sections
        );
    }

}
