package com.sephora.ecommerce.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long orderItemId;
    private Integer quantity;
    private double discount;
    private double orderedProductPrice;

    private ProductDTO product;
}
