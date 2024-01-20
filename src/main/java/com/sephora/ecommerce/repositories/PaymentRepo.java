package com.sephora.ecommerce.repositories;

import com.sephora.ecommerce.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<Payment, Long> {
}
