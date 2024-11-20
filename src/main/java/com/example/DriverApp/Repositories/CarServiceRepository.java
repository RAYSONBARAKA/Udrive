package com.example.DriverApp.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DriverApp.Entities.CarService;

public interface CarServiceRepository extends JpaRepository<CarService, Long> {
    // Optional<CarService> findByServiceName(String serviceName);
    // Optional<CarService> findByServiceName(String serviceName);
    List<CarService> findAllByServiceName(String serviceName); 
    Optional<CarService> findByServiceNameAndVehicleType(String serviceName, String vehicleType);
// Incorrect method
List<CarService> findByServiceName(String serviceName);



}



