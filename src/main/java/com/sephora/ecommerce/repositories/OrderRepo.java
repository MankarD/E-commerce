package com.sephora.ecommerce.repositories;

import com.sephora.ecommerce.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepo extends JpaRepository<Order, Long> {
//    @Query("SELECT o FROM Order o WHERE o.email = ?1 AND o.id = ?2")
    Order findByEmailAndOrderId(String emailId, Long orderId);

    List<Order> findAllByEmail(String emailId);
}
