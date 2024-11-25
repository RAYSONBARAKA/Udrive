package com.example.DriverApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.example.DriverApp.Entities.Driver;
import com.example.DriverApp.Entities.RideRequest;

import jakarta.persistence.LockModeType;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    // Find ride requests by the Driver entity
    List<RideRequest> findByDriverId(Long driverId);

    List<RideRequest> findByDriverAndStatus(Driver driver, String status);

    List<RideRequest> findByDriverAndStatusAndServiceName(Driver driver, String status, String serviceName);

    // Find pending rides by service name (old method)
    @Query("SELECT r FROM RideRequest r WHERE r.status = :status AND r.serviceName = :serviceName")
    List<RideRequest> findPendingRidesByServiceName(
            @Param("status") String status,
            @Param("serviceName") String serviceName
    );

    // New method: Find pending rides by service ID
    @Query("SELECT r FROM RideRequest r WHERE r.status = :status AND r.carService.id = :serviceId")
    List<RideRequest> findPendingRidesByServiceId(
            @Param("status") String status,
            @Param("serviceId") Long serviceId


    );


//     @Lock(LockModeType.PESSIMISTIC_WRITE)
// @Query("SELECT r FROM RideRequest r WHERE r.id = :rideRequestId")
// RideRequest findByIdWithLock(@Param("rideRequestId") Long rideRequestId);
}