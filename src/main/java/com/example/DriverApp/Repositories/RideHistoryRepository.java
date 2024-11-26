package com.example.DriverApp.Repositories;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.DriverApp.Entities.RideHistory;

public interface RideHistoryRepository extends JpaRepository<RideHistory, Long> {
    List<RideHistory> findByCustomerId(Long customerId);
    @Query("SELECT COUNT(r) FROM RideHistory r")
    long countTotalRides();

    long countByServiceName(String serviceName);
    @Query("SELECT SUM(r.totalAmount) FROM RideHistory r")
    Double calculateTotalRevenue();
 
    @Query("SELECT COALESCE(r.serviceName, 'Total rides') AS serviceName, COUNT(r) AS rideCount FROM RideHistory r GROUP BY r.serviceName")
List<Map<String, Object>> countRidesByService();

}


