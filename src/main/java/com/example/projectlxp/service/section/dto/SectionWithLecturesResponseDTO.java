package com.example.projectlxp.service.section.dto;

import com.example.projectlxp.model.section.Section;
import com.example.projectlxp.service.lecture.dto.LectureResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SectionWithLecturesResponseDTO {

    private Long id;
    private Long courseId;
    private String title;
    private Integer order;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LectureResponseDTO> lectures;

    // Constructors
    public SectionWithLecturesResponseDTO() {
    }

    public SectionWithLecturesResponseDTO(Long id, Long courseId, String title, Integer order, LocalDateTime createdAt, LocalDateTime updatedAt, List<LectureResponseDTO> lectures) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.order = order;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lectures = lectures;
    }

    // Factory method
    public static SectionWithLecturesResponseDTO from(Section section) {
        List<LectureResponseDTO> lectures = section.getLectures().stream()
                .filter(lecture -> !lecture.isDeleted())
                .map(LectureResponseDTO::from)
                .collect(Collectors.toList());

        return new SectionWithLecturesResponseDTO(
                section.getId(),
                section.getCourse().getId(),
                section.getTitle(),
                section.getOrder(),
                section.getCreatedAt(),
                section.getUpdatedAt(),
                lectures
        );
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<LectureResponseDTO> getLectures() {
        return lectures;
    }

    public void setLectures(List<LectureResponseDTO> lectures) {
        this.lectures = lectures;
    }
}
