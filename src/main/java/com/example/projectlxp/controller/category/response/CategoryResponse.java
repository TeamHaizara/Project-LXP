package com.example.projectlxp.controller.category.response;

import com.example.projectlxp.service.category.dto.CategoryServiceDto;

public record CategoryResponse(
    Long id,
    String name
) {
    // Service DTO로부터 Response DTO를 생성하는 정적 팩토리 메소드
    public static CategoryResponse from(CategoryServiceDto dto) {
        return new CategoryResponse(dto.id(), dto.name());
    }
}
