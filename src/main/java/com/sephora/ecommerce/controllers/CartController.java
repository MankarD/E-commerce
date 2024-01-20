package com.sephora.ecommerce.controllers;

import com.sephora.ecommerce.entities.Cart;
import com.sephora.ecommerce.payloads.CartDTO;
import com.sephora.ecommerce.services.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {
    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity){
        CartDTO cartDTO = cartService.addProductToCart(cartId, productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/admin/carts")
    public ResponseEntity<List<CartDTO>> getCarts(){
        List<CartDTO> cartDTOs = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOs, HttpStatus.FOUND);
    }

    @GetMapping("/public/users/{emailId}/carts/{cartId}")
    public ResponseEntity<CartDTO> getCartByIdAndEmail(@PathVariable Long cartId, @PathVariable String emailId){
        CartDTO cartDTO = cartService.getCartByCartIdAndEmailId(emailId, cartId);
        return new ResponseEntity<>(cartDTO, HttpStatus.FOUND);
    }

    @PatchMapping("/public/carts/{cartId}/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> updateCartProduct(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable Integer quantity){
        CartDTO dto = cartService.updateProductQuantityInCart(cartId, productId, quantity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/public/carts/{cartId}/products/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId){
        String status = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
