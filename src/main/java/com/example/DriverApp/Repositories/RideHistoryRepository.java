package com.example.DriverApp.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.DriverApp.Entities.RideHistory;

public interface RideHistoryRepository extends JpaRepository<RideHistory, Long> {
    List<RideHistory> findByCustomerId(Long customerId);
    @Query("SELECT COUNT(r) FROM RideHistory r")
    long countTotalRides();

    long countByServiceName(String serviceName);

    
}


