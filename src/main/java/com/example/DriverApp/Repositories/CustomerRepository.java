package com.example.DriverApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import com.example.DriverApp.Entities.Admin;
import com.example.DriverApp.Entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    //  find a customer by email
    Optional<Customer> findByEmail(String email);

    // Find a customer by phone number
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByActivationCode(String activationCode);



    
}