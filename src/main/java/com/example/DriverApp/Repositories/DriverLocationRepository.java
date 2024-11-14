package com.example.DriverApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DriverApp.Entities.DriverLocationEntity;


public interface DriverLocationRepository extends JpaRepository<DriverLocationEntity, Long> {
    DriverLocationEntity findByDriverId(Long driverId);
}

