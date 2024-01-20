package com.sephora.ecommerce.repositories;

import com.sephora.ecommerce.entities.User;
import com.sephora.ecommerce.payloads.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
//    @Query("SELECT u FROM User u JOIN FETCH u.addresses a WHERE a.addressId = ?1")
//    List<User> findByAddress(Long addressId);

}