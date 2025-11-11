package com.example.projectlxp.controller.cart;

import com.example.projectlxp.controller.cart.request.CartAddRequest;
import com.example.projectlxp.controller.cart.request.CartDeleteRequest;
import com.example.projectlxp.controller.cart.response.CartListResponse;
import com.example.projectlxp.model.cart.Cart;
import com.example.projectlxp.model.cart.CartItems;
import com.example.projectlxp.service.cart.CartService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<Void> addCart(
            @Valid @RequestBody CartAddRequest request,
            Long userId
    ) {
        cartService.addCart(request.toServiceDto(userId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping
    public ResponseEntity<CartListResponse> getCart(Long userId) {
        CartItems carts = cartService.getAllCartItems(userId);
        return ResponseEntity.ok(CartListResponse.from(carts));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCart(
            @Valid @RequestBody CartDeleteRequest request,
            Long userId
    ) {
        cartService.deleteCart(request.toServiceDto(userId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllCart(Long userId) {
        cartService.deleteAllCart(userId);
        return ResponseEntity.noContent().build();
    }
}