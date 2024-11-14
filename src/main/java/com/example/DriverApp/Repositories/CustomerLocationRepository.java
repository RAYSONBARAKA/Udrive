package com.example.DriverApp.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DriverApp.Entities.CustomerLocation;

public interface CustomerLocationRepository extends JpaRepository<CustomerLocation, Long> {
    CustomerLocation findByCustomerId(Long customerId);
}

