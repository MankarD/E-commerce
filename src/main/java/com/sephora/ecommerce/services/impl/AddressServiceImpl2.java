package com.sephora.ecommerce.services.impl;

import com.sephora.ecommerce.entities.Address;
import com.sephora.ecommerce.entities.User;
import com.sephora.ecommerce.exceptions.APIException;
import com.sephora.ecommerce.exceptions.ResourceNotFoundException;
import com.sephora.ecommerce.payloads.AddressWithUserIdDTO;
import com.sephora.ecommerce.repositories.AddressRepository;
import com.sephora.ecommerce.repositories.UserRepository;
import com.sephora.ecommerce.services.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class AddressServiceImpl2 implements AddressService {
    private AddressRepository addressRepository;
    private ModelMapper modelMapper;
    private UserRepository userRepository;

    public AddressServiceImpl2(AddressRepository addressRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    @Override
    public Set<AddressWithUserIdDTO> createAddresses(Set<AddressWithUserIdDTO> addressDTOSet) {
        Set<AddressWithUserIdDTO> savedAddressDTOs = new HashSet<>();

        for (AddressWithUserIdDTO addressDTO : addressDTOSet) {
            try {
                AddressWithUserIdDTO savedAddressDTO = createAddress(addressDTO);
                savedAddressDTOs.add(savedAddressDTO);
            } catch (APIException e) {
                log.error("Duplicate address found for userId {}: {}", addressDTO.getUserId(), e.getMessage());
            }
        }
        if (savedAddressDTOs.isEmpty()){
            throw new APIException("Duplicate Elements! No addresses were successfully saved.");
        }
        return savedAddressDTOs;
    }

    @Override
    public AddressWithUserIdDTO createAddress(AddressWithUserIdDTO addressDTO) {
        Address address = modelMapper.map(addressDTO, Address.class);
        User user = userRepository.findById(addressDTO.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User", "userId", addressDTO.getUserId()));

        // Use the countByAddressDetails method to check for duplicate addresses
        long existingAddressCount = addressRepository.countByAddressDetails(user, address.getLocalAddress(), address.getCity(), address.getState(), address.getCountry(), address.getPinCode());
        if (existingAddressCount > 0) {
            throw new APIException("Address with the same details already exists for user with userId: "+addressDTO.getUserId());
        }

        // If the address doesn't exist, save it to the database
        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        // You can return the saved address or convert it to DTO and return it
        AddressWithUserIdDTO savedAddressDTO = modelMapper.map(savedAddress, AddressWithUserIdDTO.class);
        return savedAddressDTO;
    }

    @Override
    @Cacheable(value = "addressListCache")
    public List<AddressWithUserIdDTO> getAllAddresses() {
        List<Address> addresses = addressRepository.findAll();
        List<AddressWithUserIdDTO> addressDTOs = addresses.stream()
                .map(e -> modelMapper.map(e, AddressWithUserIdDTO.class))
                .collect(Collectors.toList());
        return addressDTOs;
    }

    @Override
    @Cacheable(value = "addressCache", key = "#addressId")
    public AddressWithUserIdDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address, AddressWithUserIdDTO.class);
    }

    @Override
    @CachePut(value = "addressCache", key = "#addressId")
    @CacheEvict(value = "addressListCache", allEntries = true)
    public AddressWithUserIdDTO updateAddressById(Long addressId, AddressWithUserIdDTO addressDTO) {
        User user = userRepository.findById(addressDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", addressDTO.getUserId()));

        // Find the address to update by its ID
        Address addressToUpdate = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        // Use the countByAddressDetails method to check for duplicate addresses
        long existingAddressCount = addressRepository
                .countByAddressDetails(user, addressDTO.getLocalAddress(), addressDTO.getCity(),
                        addressDTO.getState(), addressDTO.getCountry(), addressDTO.getPinCode());
        if (existingAddressCount > 0)  {
            throw new APIException("Address with the same details already exists for this user");
        }

        // Update the address properties with the new values
        addressToUpdate.setLocalAddress(addressDTO.getLocalAddress());
        addressToUpdate.setCity(addressDTO.getCity());
        addressToUpdate.setState(addressDTO.getState());
        addressToUpdate.setCountry(addressDTO.getCountry());
        addressToUpdate.setPinCode(addressDTO.getPinCode());
        addressToUpdate.setUser(user);

        Address updatedAddress = addressRepository.save(addressToUpdate);
        return modelMapper.map(updatedAddress, AddressWithUserIdDTO.class);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "addressCache", key = "#addressId"),
            @CacheEvict(value = "addressListCache", allEntries = true)
    })
    public String deleteAddress(Long addressId) {
        Address addressFromDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        if (addressFromDB.getUser() != null) {
            User user = addressFromDB.getUser();
            user.getAddresses().remove(addressFromDB); // Remove the address from the user's list
            addressFromDB.setUser(null); // Remove the user from the address
            userRepository.save(user); // Save the user to persist changes
        }

        addressRepository.deleteById(addressId);

        return "Address deleted successfully with addressId: " + addressId;
    }


}
