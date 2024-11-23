package com.example.DriverApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.DriverApp.Entities.RideHistory;

public interface RideHistoryRepository extends JpaRepository<RideHistory, Long> {

    
}


