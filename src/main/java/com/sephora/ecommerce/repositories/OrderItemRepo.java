package com.sephora.ecommerce.repositories;

import com.sephora.ecommerce.entities.Order;
import com.sephora.ecommerce.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

}
