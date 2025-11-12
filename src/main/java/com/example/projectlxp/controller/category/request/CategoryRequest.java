package com.example.projectlxp.controller.category.request;

import com.example.projectlxp.service.category.dto.CategoryServiceDto;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @NotBlank(message = "카테고리 이름은 비워둘 수 없습니다.")
    String name
) {
    public CategoryServiceDto toDto() {
        return new CategoryServiceDto(null, this.name);
    }
}
