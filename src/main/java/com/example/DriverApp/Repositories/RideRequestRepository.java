package com.example.DriverApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Ensure this is from java.util

import com.example.DriverApp.Entities.Driver;
import com.example.DriverApp.Entities.RideRequest;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    // Find ride requests by the Driver entity
    List<RideRequest> findByDriverId(Long driverId);
    List<RideRequest> findByDriverAndStatus(Driver driver, String status);




}

