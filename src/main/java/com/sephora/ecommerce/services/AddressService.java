package com.sephora.ecommerce.services;

import com.sephora.ecommerce.entities.Address;
import com.sephora.ecommerce.payloads.AddressDTO;
import com.sephora.ecommerce.payloads.AddressWithUserIdDTO;

import java.util.List;
import java.util.Set;

public interface AddressService {
    Set<AddressWithUserIdDTO> createAddresses(Set<AddressWithUserIdDTO> addressDTOList);
    AddressWithUserIdDTO createAddress(AddressWithUserIdDTO addressDTO);
    List<AddressWithUserIdDTO> getAllAddresses();
    AddressWithUserIdDTO getAddressById(Long addressId);
    //Here using AddressDTO instead of Address
    AddressWithUserIdDTO updateAddressById(Long addressId, AddressWithUserIdDTO addressDTO);
    String deleteAddress(Long addressId);
}
