package com.sephora.ecommerce.services.impl;

import com.sephora.ecommerce.entities.Cart;
import com.sephora.ecommerce.entities.CartItem;
import com.sephora.ecommerce.entities.Product;
import com.sephora.ecommerce.exceptions.APIException;
import com.sephora.ecommerce.exceptions.ResourceNotFoundException;
import com.sephora.ecommerce.payloads.CartDTO;
import com.sephora.ecommerce.payloads.ProductDTO;
import com.sephora.ecommerce.repositories.CartItemRepository;
import com.sephora.ecommerce.repositories.CartRepository;
import com.sephora.ecommerce.repositories.ProductRepository;
import com.sephora.ecommerce.services.CartService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long cartId, Long productId, Integer quantity) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        Product product = productRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem != null){
            throw new APIException("Product " + product.getProductName() + " already exists in the cart");
        }
        if (product.getQuantity()==0){
            throw new APIException(product.getProductName()+" is not available");
        }
        if (product.getQuantity()<quantity){
            throw new APIException("Please make an order of the "+product.getProductName()+" less than or equal to the quantity "+product.getQuantity()+".");
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity()-quantity);

        cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(cartItem1 -> modelMapper.map(cartItem1.getProduct(), ProductDTO.class)).collect(Collectors.toList());
        cartDTO.setProductDTOList(productDTOs);
        return cartDTO;
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
