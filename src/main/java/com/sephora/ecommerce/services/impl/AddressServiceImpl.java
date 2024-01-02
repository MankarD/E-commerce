//package com.sephora.ecommerce.services.impl;
//
//import com.sephora.ecommerce.entities.Address;
//import com.sephora.ecommerce.entities.User;
//import com.sephora.ecommerce.exceptions.APIException;
//import com.sephora.ecommerce.exceptions.ResourceNotFoundException;
//import com.sephora.ecommerce.payloads.AddressDTO;
//import com.sephora.ecommerce.repositories.AddressRepository;
//import com.sephora.ecommerce.repositories.UserRepository;
//import com.sephora.ecommerce.services.AddressService;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class AddressServiceImpl implements AddressService {
//    private AddressRepository addressRepository;
//    private ModelMapper modelMapper;
//    private UserRepository userRepository;
//
//    public AddressServiceImpl(AddressRepository addressRepository, ModelMapper modelMapper, UserRepository userRepository) {
//        this.addressRepository = addressRepository;
//        this.modelMapper = modelMapper;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public AddressDTO createAddress(AddressDTO addressDTO) {
//        String localAddress = addressDTO.getLocalAddress();
//        String city = addressDTO.getCity();
//        String state = addressDTO.getState();
//        String country = addressDTO.getCountry();
//        String pinCode = addressDTO.getPinCode();
//
//        Address addressFromDB = addressRepository.findByLocalAddressAndCityAndStateAndCountryAndPinCode(localAddress, city, state, country, pinCode);
//
//        if (addressFromDB != null) {
//            throw new APIException("Address already exists with addressId: " + addressFromDB.getAddressId());
//        }
//
//        Address address = modelMapper.map(addressDTO, Address.class);
//
//        Address savedAddress = addressRepository.save(address);
//
//        return modelMapper.map(savedAddress, AddressDTO.class);
//    }
//
//    @Override
//    public List<AddressDTO> getAllAddresses() {
//        List<Address> addresses = addressRepository.findAll();
//        List<AddressDTO> addressDTOs = addresses.stream().map(e -> modelMapper.map(e, AddressDTO.class)).collect(Collectors.toList());
//        return addressDTOs;
//    }
//
//    @Override
//    public AddressDTO getAddressById(Long addressId) {
//        Address address = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
//        return modelMapper.map(address, AddressDTO.class);
//    }
//
//    @Override
//    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {
//        Address addressFromDB = addressRepository.findByLocalAddressAndCityAndStateAndCountryAndPinCode(
//                addressDTO.getLocalAddress(), addressDTO.getCity(), addressDTO.getState(),
//                addressDTO.getCountry(), addressDTO.getPinCode()
//        );
//        if (addressFromDB == null) {
//            addressFromDB = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
//            addressFromDB.setLocalAddress(addressDTO.getLocalAddress());
//            addressFromDB.setCity(addressDTO.getCity());
//            addressFromDB.setState(addressDTO.getState());
//            addressFromDB.setCountry(addressDTO.getCountry());
//            addressFromDB.setPinCode(addressDTO.getPinCode());
//
//            Address updatedAddress = addressRepository.save(addressFromDB);
//            return modelMapper.map(updatedAddress, AddressDTO.class);
//        } else {
//            List<User> users = userRepository.findByAddress(addressId);
//            final Address a = addressFromDB;
//            users.forEach(user -> user.getAddresses().add(a));
//            deleteAddress(addressId);
//            return modelMapper.map(addressFromDB, AddressDTO.class);
//        }
//    }
//
//    @Override
//    public String deleteAddress(Long addressId) {
//        // Retrieve the address to be deleted
//        Address addressFromDB = addressRepository.findById(addressId)
//                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
//
//        // Retrieve and update all users that reference this address
//        List<User> users = userRepository.findByAddress(addressId);
//        if (users != null && !users.isEmpty()) {
//            for (User user : users) {
//                user.getAddresses().remove(addressFromDB); // Remove the address from the user
//                userRepository.save(user); // Save the updated user
//            }
//        }
//
//        // Clear the users collection from the address
//        if (addressFromDB.getUsers() != null) {
//            addressFromDB.getUsers().clear();
//            addressRepository.save(addressFromDB); // Save the updated address
//        }
//
//        // Delete the address
//        addressRepository.deleteById(addressId);
//        return "Address deleted successfully with addressId: " + addressId;
//    }
//
//}
