package com.sephora.ecommerce.services;

import com.sephora.ecommerce.payloads.CartDTO;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long cartId, Long productId, Integer quantity);
    List<CartDTO> getAllCarts();
    CartDTO getCartByCartIdAndEmailId(String emailId, Long cartId);
    CartDTO updateProductInCarts(Long cartId, Long productId, Integer quantity);
    void updateProductInCarts(Long cartId, Long productId);
    String deleteProductFromCart(Long cartId, Long productId);
}
