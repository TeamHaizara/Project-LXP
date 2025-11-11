package com.example.projectlxp.service.cart;

import com.example.projectlxp.model.cart.Cart;
import com.example.projectlxp.service.cart.dto.CartServiceDto;
import java.util.List;

public interface CartService {
    void addCart(CartServiceDto dto);
    void deleteCart(CartServiceDto dto);
    void deleteAllCart(Long userId);;
    List<Cart> getAllCarts(Long userId);
}
