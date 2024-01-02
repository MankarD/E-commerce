package com.sephora.ecommerce.repositories;

import com.sephora.ecommerce.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
