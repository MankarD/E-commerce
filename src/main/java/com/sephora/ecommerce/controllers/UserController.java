package com.sephora.ecommerce.controllers;

import com.sephora.ecommerce.payloads.UserDTO;
import com.sephora.ecommerce.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")   //for authcontroller
    public ResponseEntity<UserDTO> registerHandler(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = userService.registerUser(userDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/admin/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> allUsers = userService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.FOUND);
    }

    @GetMapping("/public/users/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId){
        UserDTO userById = userService.getUserById(userId);
        return new ResponseEntity<>(userById, HttpStatus.FOUND);
    }

    @PutMapping("/public/users/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO){
        UserDTO updatedUser = userService.updateUser(userId, userDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        String status = userService.deleteUser(userId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }


}
