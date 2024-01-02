package com.sephora.ecommerce.controllers;

import com.sephora.ecommerce.payloads.AddressDTO;
import com.sephora.ecommerce.payloads.AddressWithUserIdDTO;
import com.sephora.ecommerce.services.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
public class AddressController {
    private AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping("/addresses")
    public ResponseEntity<Set<AddressWithUserIdDTO>> createAddresses(@RequestBody Set<AddressWithUserIdDTO> addressDTOSet){
       Set<AddressWithUserIdDTO> savedAddressDTOs = addressService.createAddresses(addressDTOSet);
        return new ResponseEntity<>(savedAddressDTOs, HttpStatus.CREATED);
    }

    @PostMapping("/address")
    public ResponseEntity<AddressWithUserIdDTO> createAddress(@RequestBody AddressWithUserIdDTO addressDTO){
        AddressWithUserIdDTO savedAddressDTO = addressService.createAddress(addressDTO);
        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("addresses")
    public ResponseEntity<List<AddressWithUserIdDTO>> getAllAddress(){
        List<AddressWithUserIdDTO> addressDTOS = addressService.getAllAddresses();
        return new ResponseEntity<>(addressDTOS, HttpStatus.FOUND);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressWithUserIdDTO> getAddressById(@PathVariable long addressId){
        AddressWithUserIdDTO addressDTO = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.FOUND);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressWithUserIdDTO> updateAddressById(@PathVariable long addressId, @RequestBody AddressWithUserIdDTO addressDTO){
        AddressWithUserIdDTO updatedAddressDTO = addressService.updateAddressById(addressId, addressDTO);
        return new ResponseEntity<>(updatedAddressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddressById(@PathVariable long addressId){
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
