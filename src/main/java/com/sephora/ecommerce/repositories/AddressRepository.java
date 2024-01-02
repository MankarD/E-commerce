package com.sephora.ecommerce.repositories;

import com.sephora.ecommerce.entities.Address;
import com.sephora.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT COUNT(a) FROM Address a WHERE a.user = :user AND a.localAddress = :localAddress AND a.city = :city AND a.state = :state AND a.country = :country AND a.pinCode = :pinCode")
    long countByAddressDetails(User user, String localAddress, String city, String state, String country, String pinCode);

}
