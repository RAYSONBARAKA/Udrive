package com.example.DriverApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.example.DriverApp.Entities.Driver;
import com.example.DriverApp.Entities.RideRequest;

 
public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {
    // Find ride requests by the Driver entity
    List<RideRequest> findByDriverId(Long driverId);

    
    @Query("SELECT rr FROM RideRequest rr " +
    "JOIN FETCH rr.carService cs " +
    "JOIN FETCH rr.customer c " +
    "JOIN FETCH rr.driverDetails dd " +
    "WHERE rr.driver.id = :driverId " +
    "AND rr.customer.id = :customerId")
Optional<RideRequest> findByDriverIdAndCustomerId(@Param("driverId") Long driverId, @Param("customerId") Long customerId);


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

 
}