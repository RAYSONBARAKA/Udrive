package com.example.DriverApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import com.example.DriverApp.Entities.Admin;
import com.example.DriverApp.Entities.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    //  find a customer by email
    Optional<Customer> findByEmail(String email);

    // Find a customer by phone number
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByActivationCode(String activationCode);
  @Query("SELECT COUNT(c) FROM Customer c WHERE c.isActive = true")
    long countActiveCustomers();


    
}