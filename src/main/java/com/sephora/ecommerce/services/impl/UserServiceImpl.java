//package com.sephora.ecommerce.services.impl;
//
//import com.sephora.ecommerce.config.AppConstants;
//import com.sephora.ecommerce.entities.Address;
//import com.sephora.ecommerce.entities.Role;
//import com.sephora.ecommerce.entities.User;
//import com.sephora.ecommerce.exceptions.APIException;
//import com.sephora.ecommerce.exceptions.ResourceNotFoundException;
//import com.sephora.ecommerce.payloads.AddressDTO;
//import com.sephora.ecommerce.payloads.UserDTO;
//import com.sephora.ecommerce.repositories.AddressRepository;
//import com.sephora.ecommerce.repositories.RoleRepository;
//import com.sephora.ecommerce.repositories.UserRepository;
//import com.sephora.ecommerce.services.UserService;
//import org.modelmapper.ModelMapper;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class UserServiceImpl implements UserService {
//
//    private UserRepository userRepository;
//    private RoleRepository roleRepository;
//    private ModelMapper modelMapper;
//    private AddressRepository addressRepository;
//
//    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ModelMapper modelMapper, AddressRepository addressRepository) {
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//        this.modelMapper = modelMapper;
//        this.addressRepository = addressRepository;
//    }
//
//
//    @Override
//    public UserDTO registerUser(UserDTO userDTO) {
//        try {
//            User user = modelMapper.map(userDTO, User.class);
//
//            Role role = roleRepository.findById(AppConstants.USER_ID).get();
//            user.getRoles().add(role);
//
//            String localAddress = userDTO.getAddressDTO().getLocalAddress();
//            String city = userDTO.getAddressDTO().getCity();
//            String state = userDTO.getAddressDTO().getState();
//            String country = userDTO.getAddressDTO().getCountry();
//            String pinCode = userDTO.getAddressDTO().getPinCode();
//
//            Address address = addressRepository.findByLocalAddressAndCityAndStateAndCountryAndPinCode(localAddress, city, state, country, pinCode);
//            if(address==null){
//                address = new Address(localAddress, city, state, country, pinCode);
//                addressRepository.save(address);
//            }
//
//            user.setAddresses(List.of(address));
//
//            User registeredUser = userRepository.save(user);
//
//            userDTO = modelMapper.map(registeredUser, UserDTO.class);
//
//            userDTO.setAddressDTO(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
//            return userDTO;
//        } catch (DataIntegrityViolationException e){
//            throw new APIException("User already exists with emailId: "+userDTO.getEmail());
//        }
//    }
//
//    @Override
//    public List<UserDTO> getAllUsers() {
//        List<User> users = userRepository.findAll();
//        List<UserDTO> usersDTO = users.stream().map(e -> {
//            UserDTO dto = modelMapper.map(e, UserDTO.class);
//            return dto;
//        }).collect(Collectors.toList());
//        return usersDTO;
//    }
//
//    @Override
//    public UserDTO getUserById(Long userId) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
//        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
//        return userDTO;
//    }
//
//    @Override
//    public UserDTO updateUser(Long userId, UserDTO userDTO) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
//        user.setUserName(userDTO.getUserName());
//        user.setMobileNumber(userDTO.getMobileNumber());
//        user.setEmail(userDTO.getEmail());
//        user.setPassword(userDTO.getPassword());
//
//        userDTO = modelMapper.map(user, UserDTO.class);
//
//        return userDTO;
//    }
//
//    @Override
//    public String deleteUser(Long userId) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
//        userRepository.delete(user);
//        return "User with userId "+userId+" deleted successfully!!!";
//    }
//}
