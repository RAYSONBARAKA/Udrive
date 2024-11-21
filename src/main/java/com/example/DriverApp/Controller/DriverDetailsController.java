package com.example.DriverApp.Controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.DriverApp.Service.RideService;

@RestController
@RequestMapping("/api/open/driver-details")
public class DriverDetailsController {
      private static final Logger LOGGER = LoggerFactory.getLogger(DriverDetailsController.class);

    private final RideService rideService;

    // Constructor-based dependency injection
    public DriverDetailsController(RideService rideService) {
        this.rideService = rideService;
    }

    /**
     * Endpoint to fetch recent driver details by customer ID
     *
     * @param customerId The ID of the customer
     * @return ResponseEntity containing recent driver details
     */
    @GetMapping("/recent/{customerId}")
    public ResponseEntity<Map<String, Object>> getRecentDriverDetails(@PathVariable Long customerId) {
        LOGGER.info("Received request to fetch recent driver details for customer ID {}", customerId);

        try {
            return rideService.getRecentDriverDetailsByCustomerId(customerId);
        } catch (RuntimeException e) {
            LOGGER.error("Error fetching recent driver details for customer ID {}: {}", customerId, e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "404 NOT FOUND");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }   
}
