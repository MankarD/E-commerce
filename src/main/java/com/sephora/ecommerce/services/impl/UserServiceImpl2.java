package com.sephora.ecommerce.services.impl;

import com.sephora.ecommerce.config.AppConstants;
import com.sephora.ecommerce.entities.Address;
import com.sephora.ecommerce.entities.Cart;
import com.sephora.ecommerce.entities.Role;
import com.sephora.ecommerce.entities.User;
import com.sephora.ecommerce.exceptions.APIException;
import com.sephora.ecommerce.exceptions.ResourceNotFoundException;
import com.sephora.ecommerce.payloads.*;
import com.sephora.ecommerce.repositories.AddressRepository;
import com.sephora.ecommerce.repositories.CartRepository;
import com.sephora.ecommerce.repositories.RoleRepository;
import com.sephora.ecommerce.repositories.UserRepository;
import com.sephora.ecommerce.services.UserService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserServiceImpl2 implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private ModelMapper modelMapper;
    private AddressRepository addressRepository;
    @Autowired
    private CartRepository cartRepository;

    public UserServiceImpl2(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
        this.addressRepository = addressRepository;
    }

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        log.info("Converting UserDTO to User Entity...");

        // Retrieve the default role or handle role assignment as needed
        Role role = roleRepository.findById(AppConstants.USER_ID).get();
        user.getRoles().add(role);
        log.info("Adding Role: USER to the new user...");

        // Use streams to map and save addresses, then collect them into a set
        Set<Address> savedAddresses = userDTO.getAddresses().stream()
                .map(addressDTO -> {
                    Address address = modelMapper.map(addressDTO, Address.class);
                    address.setUser(user);
                    return addressRepository.save(address);
                })
                .collect(Collectors.toSet());

        user.setAddresses(savedAddresses);
        log.info("setting address/es to the User...");

        Cart cart = new Cart();
        user.setCart(cart);
        log.info("creating cart for the user...");

        try {
            User registeredUser = userRepository.save(user);
            cart.setUser(registeredUser);
            log.info("saving user to the database...");
            return modelMapper.map(registeredUser, UserDTO.class);
        } catch (DataIntegrityViolationException e) {
            throw new APIException("User already exists with emailId: " + userDTO.getEmail());
        }
    }

    @Override
    @Cacheable(value = "userListCache", key = "#pageNumber + '-' + #pageSize + '-' + #sortBy + '-' + #sortOrder")
    public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<User> pageUsers = userRepository.findAll(pageDetails);
        List<User> users = pageUsers.getContent();

        List<UserWithCartDTO> usersDTOs = users.stream().map(user -> {
            UserWithCartDTO dto = modelMapper.map(user, UserWithCartDTO.class);
            //This shows only one address during getAll
            for (Address address : user.getAddresses()) {
                AddressDTO firstAddress = modelMapper.map(address, AddressDTO.class);
                dto.setAddresses(Collections.singleton(firstAddress));
                break; // Exit the loop after processing the first address
            }

            CartDTO cartDTO = modelMapper.map(user.getCart(), CartDTO.class);
            dto.setCartDTO(cartDTO);

            return dto;
        }).collect(Collectors.toList());

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(usersDTOs);
        userResponse.setPageNumber(pageUsers.getNumber());
        userResponse.setPageSize(pageUsers.getSize());
        userResponse.setTotalElements(pageUsers.getTotalElements());
        userResponse.setTotalPages(pageUsers.getTotalPages());
        userResponse.setLastPage(pageUsers.isLast());

        return userResponse;
    }

    @Override
    @Cacheable(value = "userCache", key = "#userId")
    public UserWithCartDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        UserWithCartDTO userDTO = modelMapper.map(user, UserWithCartDTO.class);

        log.info("getting the user...");
        log.info("Welcome message...");
        CartDTO cartDTO = modelMapper.map(user.getCart(), CartDTO.class);
        userDTO.setCartDTO(cartDTO);

        return userDTO;
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "userCache", key = "#userId"),
        @CacheEvict(value = "userListCache", allEntries = true)
    })
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        // Check if the provided addresses belong to the user
        for (AddressDTO addressDTO : userDTO.getAddresses()) {
            if (!user.getAddresses().contains(modelMapper.map(addressDTO, Address.class))) {
                throw new APIException("One or more addresses do not belong to the user.");
            }
        }

        user.setUserName(userDTO.getUserName());
        user.setMobileNumber(userDTO.getMobileNumber());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());

        // Create a set to store the updated addresses
        Set<Address> updatedAddresses = new HashSet<>();

        // Map and save the updated addresses
        for (AddressDTO addressDTO : userDTO.getAddresses()) {
            Address updatedAddress = modelMapper.map(addressDTO, Address.class);
            updatedAddress.setUser(user);
            updatedAddresses.add(updatedAddress);
        }

        user.setAddresses(updatedAddresses);

        // Save the user entity to update the changes in the database
        user = userRepository.save(user);

        // Map the updated user back to a UserDTO
        UserDTO updatedUserDTO = modelMapper.map(user, UserDTO.class);

        // Map the updated addresses to AddressDTO
        updatedUserDTO.setAddresses(updatedAddresses.stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .collect(Collectors.toSet()));

        return updatedUserDTO;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "userCache", key = "#userId"),
            @CacheEvict(value = "userListCache", allEntries = true)
    })
    public String deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        userRepository.delete(user);
        return "User with userId " + userId + " deleted successfully!!!";
    }
}
