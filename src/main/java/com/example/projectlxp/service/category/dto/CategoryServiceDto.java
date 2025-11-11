package com.example.projectlxp.service.category.dto;

import com.example.projectlxp.model.category.Category;

public record CategoryServiceDto(
    Long id,
    String name
) {
    public Category toEntity() {
        // new 키워드 대신 Category의 정적 팩토리 메소드를 사용하여 엔티티를 생성합니다.
        return Category.create(this.name);
    }
}
