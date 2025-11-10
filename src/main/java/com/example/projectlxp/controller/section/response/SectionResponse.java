package com.example.projectlxp.controller.section.response;

import com.example.projectlxp.model.section.Section;

public record SectionResponse(
    Long id,
    String title,
    Integer order
) {
    public static SectionResponse from(Section section) {
        return new SectionResponse(section.getId(), section.getTitle(), section.getOrder());
    }
}
