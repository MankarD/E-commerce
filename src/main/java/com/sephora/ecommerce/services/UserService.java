package com.sephora.ecommerce.services;

import com.sephora.ecommerce.entities.User;
import com.sephora.ecommerce.payloads.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    List<UserDTO> getAllUsers();
    UserDTO getUserById(Long userId);
    UserDTO updateUser(Long userId, UserDTO userDTO);
    String deleteUser(Long userId);
}
