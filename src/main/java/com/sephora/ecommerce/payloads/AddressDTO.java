package com.sephora.ecommerce.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long addressId;
    private String localAddress;
    private String city;
    private String state;
    private String country;
    private String pinCode;
}
