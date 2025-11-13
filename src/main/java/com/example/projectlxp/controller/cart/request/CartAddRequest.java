package com.example.projectlxp.controller.cart.request;

import com.example.projectlxp.service.cart.dto.CartServiceDto;
import jakarta.validation.constraints.NotNull;

public record CartAddRequest(
        @NotNull(message = "강좌 ID는 필수입니다.")
        Long courseId
) implements CartRequest {

    @Override
    public CartServiceDto toServiceDto(Long userId) {
        return new CartServiceDto(userId, courseId);
    }
}