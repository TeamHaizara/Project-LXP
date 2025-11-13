package com.example.projectlxp.controller.cart;

import com.example.projectlxp.controller.cart.request.CartAddRequest;
import com.example.projectlxp.controller.cart.request.CartDeleteRequest;
import com.example.projectlxp.controller.cart.response.CartListResponse;
import com.example.projectlxp.model.cart.CartItems;
import com.example.projectlxp.service.cart.CartService;
import com.example.projectlxp.service.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/learner/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<Void> addCart(
            @Valid @RequestBody CartAddRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        cartService.addCart(request.toServiceDto(userDetails.getUserId()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping
    public ResponseEntity<CartListResponse> getCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        CartItems carts = cartService.getAllCartItems(userDetails.getUserId());
        return ResponseEntity.ok(CartListResponse.from(carts));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCart(
            @Valid @RequestBody CartDeleteRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        cartService.deleteCart(request.toServiceDto(userDetails.getUserId()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.deleteAllCart(userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}