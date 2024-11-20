package com.example.DriverApp.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DriverApp.Entities.CarService;

public interface CarServiceRepository extends JpaRepository<CarService, Long> {
    
    // Find all services by serviceName (used when multiple results are expected)
    List<CarService> findAllByServiceName(String serviceName);

    // Find a single service by serviceName (used when expecting one result)
    Optional<CarService> findByServiceName(String serviceName);

    // Find by serviceName and vehicleType
    Optional<CarService> findByServiceNameAndVehicleType(String serviceName, String vehicleType);
}
