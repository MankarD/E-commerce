package com.sephora.ecommerce.repositories;

import com.sephora.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
//    @Query("SELECT u FROM User u JOIN FETCH u.addresses a WHERE a.addressId = ?1")
//    List<User> findByAddress(Long addressId);
}