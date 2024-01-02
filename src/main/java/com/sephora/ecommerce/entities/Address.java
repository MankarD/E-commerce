package com.sephora.ecommerce.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "addresses")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long addressId;
    private String localAddress;
    private String city;
    private String state;
    private String country;
    private String pinCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private User user;

}
