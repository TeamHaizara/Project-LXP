package com.example.projectlxp.controller.cart.response;

import com.example.projectlxp.model.cart.Cart;
import com.example.projectlxp.model.cart.CartItems;

import java.time.LocalDateTime;
import java.util.List;

public record CartListResponse(List<CartItem> items, int itemCount) {

    public static CartListResponse from(CartItems carts) {
        List<CartItem> items = carts.toList().stream().map(CartItem::from).toList();
        return new CartListResponse(items, carts.size());
    }

    public record CartItem(Long cartId, Long courseId, LocalDateTime addedAt) {
        private static CartItem from(Cart cart) {
            return new CartItem(cart.getId(), cart.getCartCourseId(), cart.getCreatedAt());
        }
    }
}