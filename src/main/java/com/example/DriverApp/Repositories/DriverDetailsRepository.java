package com.example.DriverApp.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DriverApp.Entities.DriverDetails;

@Repository
public interface DriverDetailsRepository extends JpaRepository<DriverDetails, Long> {

    Optional<DriverDetails> findByDriverIdAndRideRequestId(Long driverId, Long rideRequestId);

    Optional<DriverDetails> findTopByCustomerIdOrderByCreatedAtDesc(Long customerId);
    Optional<DriverDetails> findTopByCustomerIdOrderByIdDesc(Long customerId);
    DriverDetails findByRideRequestId(Long rideRequestId);
    Optional<DriverDetails> findTopByCustomerIdAndStatusOrderByIdDesc(Long customerId, String status);
    boolean existsByDriverIdAndCustomerId(Long driverId, Long customerId);
    Optional<DriverDetails> findByCustomerIdAndStatus(Long customerId, String status);
 

 }