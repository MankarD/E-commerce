package com.sephora.ecommerce.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartDTO implements Serializable {
    private Long cartId;
    private Double totalPrice = 0.0;

    private List<ProductDTO> productDTOList = new ArrayList<>();
}
