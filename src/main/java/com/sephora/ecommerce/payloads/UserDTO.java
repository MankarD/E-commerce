package com.sephora.ecommerce.payloads;

import com.sephora.ecommerce.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String userName;
    private String mobileNumber;
    private String email;
    private String password;
    private Set<Role> roles = new HashSet<>();
    private Set<AddressDTO> addresses = new HashSet<>();
    private CartDTO cartDTO;
}
