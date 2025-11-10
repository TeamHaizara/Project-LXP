package com.example.projectlxp.controller.section.request;

import com.example.projectlxp.service.section.dto.SectionServiceDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SectionCreateRequest(
    @NotBlank(message = "섹션 제목은 비워둘 수 없습니다.")
    String title,

    @NotNull(message = "섹션 순서는 비워둘 수 없습니다.")
    Integer order
) {
    public SectionServiceDto toDto(Long courseId) {
        return new SectionServiceDto(courseId, this.title, this.order);
    }
}
