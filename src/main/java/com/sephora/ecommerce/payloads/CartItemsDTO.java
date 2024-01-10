package com.sephora.ecommerce.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemsDTO {
    private Long cartItemId;
    private Integer quantity;
    private Double discount;
    private Double productPrice;

    private CartDTO cartDTO;
    private ProductDTO productDTO;
}
