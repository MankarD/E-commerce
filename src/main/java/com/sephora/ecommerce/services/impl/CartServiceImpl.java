package com.sephora.ecommerce.services.impl;

import com.sephora.ecommerce.payloads.CartDTO;
import com.sephora.ecommerce.services.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class CartServiceImpl implements CartService {
    @Override
    public CartDTO addProductToCart(Long cartId, Long productId, Integer quantity) {
        return null;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        return null;
    }

    @Override
    public CartDTO getCartByCartIdAndEmailId(String emailId, Long cartId) {
        return null;
    }

    @Override
    public CartDTO updateProductInCarts(Long cartId, Long productId, Integer quantity) {
        return null;
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {

    }

    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        return null;
    }
}
