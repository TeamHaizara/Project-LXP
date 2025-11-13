package com.example.projectlxp.model.cart;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class CartItems {

    private final List<Cart> items;

    private CartItems(List<Cart> items) {
        this.items = items.stream()
                .filter(Objects::nonNull)
                .filter(cart -> cart.getDeletedAt() == null)
                .toList();
    }

    public static CartItems from(List<Cart> carts) {
        return new CartItems(carts == null ? List.of() : carts);
    }

    public int size() {
        return items.size();
    }
    public List<Cart> toList(){
        return items;
    }
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public CartItems filterByCourseId(Long courseId) {
        return new CartItems(items.stream()
                .filter(cart -> cart.getCartCourseId().equals(courseId))
                .toList());
    }

    public CartItems sortByAddedAtDesc() {
        return new CartItems(items.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList());
    }

    public LocalDateTime latestAddedAtOrNull() {
        return items.stream()
                .map(Cart::getCreatedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
}