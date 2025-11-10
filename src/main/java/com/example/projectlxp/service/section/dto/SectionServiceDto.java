package com.example.projectlxp.service.section.dto;

import com.example.projectlxp.model.course.Course;
import com.example.projectlxp.model.section.Section;

public record SectionServiceDto(
    Long courseId,
    String title,
    Integer order
) {
    public Section toEntity(Course course) {
        return Section.create(course, title, order);
    }
}
