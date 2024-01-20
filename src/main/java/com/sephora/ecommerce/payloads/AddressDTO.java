package com.sephora.ecommerce.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO implements Serializable {
    private Long addressId;
    private String localAddress;
    private String city;
    private String state;
    private String country;
    private String pinCode;
}
