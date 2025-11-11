package com.example.projectlxp.service.cart.dto;

import com.example.projectlxp.model.cart.Cart;
import java.time.LocalDateTime;

public record CartServiceDto(
        Long userId,
        Long courseId
) {

    public Cart toEntity() {
        return Cart.of(userId, courseId);
    }
}