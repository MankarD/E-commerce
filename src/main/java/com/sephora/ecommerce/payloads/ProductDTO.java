package com.sephora.ecommerce.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements Serializable {

    private Long productId;
    private String productName;
    private String image;
    private String description;
    private Integer quantity;
    private Double price;
    private Double discount;
    private Double specialPrice;

    private Set<String> categoryNames;
}
