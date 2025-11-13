package com.example.projectlxp.controller.cart.request;

import com.example.projectlxp.service.cart.dto.CartServiceDto;

public interface CartRequest {
    CartServiceDto toServiceDto(Long userId);
}