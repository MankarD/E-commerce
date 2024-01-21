package com.sephora.ecommerce.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemsDTO implements Serializable {
    private Long cartItemId;
    private Integer quantity;
    private Double discount;
    private Double productPrice;

    private CartDTO cartDTO;
    private ProductDTO productDTO;
}
