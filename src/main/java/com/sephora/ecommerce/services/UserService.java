package com.sephora.ecommerce.services;

import com.sephora.ecommerce.entities.User;
import com.sephora.ecommerce.payloads.UserDTO;
import com.sephora.ecommerce.payloads.UserResponse;
import com.sephora.ecommerce.payloads.UserWithCartDTO;

import java.util.List;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    UserWithCartDTO getUserById(Long userId);
    UserDTO updateUser(Long userId, UserDTO userDTO);
    String deleteUser(Long userId);
}
