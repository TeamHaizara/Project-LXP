package com.example.projectlxp.controller.section.response;

import com.example.projectlxp.controller.lecture.response.LectureResponse;
import com.example.projectlxp.model.section.Section;

import java.util.List;
import java.util.stream.Collectors;

public record SectionDetailResponse(
    Long id,
    String title,
    Integer order,
    List<LectureResponse> lectures
) {
    public static SectionDetailResponse from(Section section) {
        List<LectureResponse> lectures = section.getLectures().stream()
                .filter(lecture -> lecture.getDeletedAt() == null)
                .map(LectureResponse::from)
                .collect(Collectors.toList());
        
        return new SectionDetailResponse(
                section.getId(), 
                section.getTitle(), 
                section.getOrder(),
                lectures
        );
    }
}
