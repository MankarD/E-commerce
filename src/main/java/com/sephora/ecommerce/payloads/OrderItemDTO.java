package com.sephora.ecommerce.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO implements Serializable {
    private Long orderItemId;
    private Integer quantity;
    private double discount;
    private double orderedProductPrice;

    private ProductDTO product;
}
