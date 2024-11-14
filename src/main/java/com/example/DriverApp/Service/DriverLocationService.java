package com.example.DriverApp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.DriverApp.Entities.DriverLocationEntity;
import com.example.DriverApp.Repositories.DriverLocationRepository;

@Service
public class DriverLocationService {

    @Autowired
    private DriverLocationRepository locationRepository;

    // Method to get the driver's location based on the JWT
    public DriverLocationEntity getDriverLocation() {
        // Get the currently authenticated driver's ID from the JWT
        Long driverId = getCurrentDriverId();

        // Fetch the driver's location based on the driverId from the JWT
        return locationRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver location not found for id " + driverId));
    }

    // Method to update the driver's location
    public void updateDriverLocation(DriverLocationEntity locationData) {
        // Get the currently authenticated driver's ID from the JWT
        Long driverId = getCurrentDriverId();

        // Check if the location data corresponds to the authenticated driver
        if (!locationData.getDriverId().equals(driverId)) {
            throw new RuntimeException("Location update does not belong to the authenticated driver.");
        }

        // Save or update the driver's location in the repository
        locationRepository.save(locationData);
    }

    // Private method to get the current driver ID from the authentication token
    private Long getCurrentDriverId() {
        // Retrieve the driver's ID from the authentication token
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(userId);  // Assuming the user ID is stored as a String in the token
    }
}
