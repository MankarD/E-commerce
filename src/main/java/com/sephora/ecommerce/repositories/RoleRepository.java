package com.sephora.ecommerce.repositories;

import com.sephora.ecommerce.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
