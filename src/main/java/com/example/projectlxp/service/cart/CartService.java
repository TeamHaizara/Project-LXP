package com.example.projectlxp.service.cart;

import com.example.projectlxp.model.cart.CartItems;
import com.example.projectlxp.service.cart.dto.CartServiceDto;

public interface CartService {
    void addCart(CartServiceDto dto);
    void deleteCart(CartServiceDto dto);
    void deleteAllCart(Long userId);;
    CartItems getAllCartItems(Long userId);
}
