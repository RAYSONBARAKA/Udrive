package com.example.DriverApp.Repositories;

import java.util.Optional;
import java.util.List; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DriverApp.Entities.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmail(String email);
    Optional<Driver> findByPhoneNumber(String phoneNumber);
    Optional<Driver> findByStatus(String status);

    List<Driver> findByLatitudeBetweenAndLongitudeBetween(
        double lat1, double lat2, double lon1, double lon2);
        List<Driver> findByAvailable(boolean available);
        List<Driver> findByAvailableTrue();
        List<Driver> findByRole(String role);
        List<Driver> findByRoleAndAvailable(String role, boolean available);
        List<Driver> findByRoleAndAvailableAndActive(String role, boolean available, boolean active);
        public Driver findFirstByServiceNameAndRoleAndAvailable(String serviceName, String role, boolean available);
        Driver findFirstByServiceNameAndAvailable(String serviceName, boolean available);
        Driver findFirstByRoleAndAvailable(String role, boolean available);
        List<Driver> findByVehicleTypeAndIsActiveTrue(String vehicleType);
        List<Driver> findByVehicleTypeAndActive(String vehicleType, boolean active);





}


