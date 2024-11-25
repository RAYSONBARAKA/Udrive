package com.example.DriverApp.Controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DriverApp.Entities.CarService;
import com.example.DriverApp.Entities.Customer;
import com.example.DriverApp.Entities.DriverDetails;
import com.example.DriverApp.Entities.RideHistory;
import com.example.DriverApp.Entities.RideRequest;
import com.example.DriverApp.Repositories.DriverDetailsRepository;
import com.example.DriverApp.Repositories.RideHistoryRepository;
import com.example.DriverApp.Repositories.RideRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/api/open/trips")
public class TripController {


    private static final Logger LOGGER = LoggerFactory.getLogger(TripController.class);

     @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private DriverDetailsRepository driverDetailsRepository;

    @Autowired
    private RideHistoryRepository rideHistoryRepository;

     @Autowired
    public TripController(
            RideRequestRepository rideRequestRepository,
            DriverDetailsRepository driverDetailsRepository,
            RideHistoryRepository rideHistoryRepository) {
        this.rideRequestRepository = rideRequestRepository;
        this.driverDetailsRepository = driverDetailsRepository;
        this.rideHistoryRepository = rideHistoryRepository;
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Earth radius in kilometers
    
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
    
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
        return EARTH_RADIUS * c; // Convert to distance in kilometers
    }
    

    @PostMapping("/end")
    public ResponseEntity<?> endTrip(@RequestParam Long rideRequestId) {
        try {
            // Fetch RideRequest
            RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                    .orElseThrow(() -> new RuntimeException("RideRequest not found"));

            // Ensure the ride request has an associated car service
            CarService carService = rideRequest.getCarService();
            if (carService == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"CarService is not associated with this ride request\"}");
            }

            // Fetch Customer details
            Customer customer = rideRequest.getCustomer();
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Customer not associated with the ride request\"}");
            }

            // Fetch DriverDetails and update status
            DriverDetails driverDetails = rideRequest.getDriverDetails();
            if (driverDetails == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Driver details not associated with the ride request\"}");
            }

            if ("incomplete".equals(driverDetails.getStatus())) {
                driverDetails.setStatus("completed");
                driverDetailsRepository.save(driverDetails); // Save updated driver status
            } else {
                LOGGER.warn("Driver status is already set to: {}", driverDetails.getStatus());
            }

            // Calculate distance
            double distance = calculateDistance(
                    customer.getLatitude(), customer.getLongitude(),
                    rideRequest.getDropOffLatitude(), rideRequest.getDropOffLongitude()
            );

            // Calculate total amount
            double totalAmount = distance * carService.getRatePerKm(); // Total amount based on distance and rate per km

            // Update RideRequest status to "COMPLETED"
            rideRequest.setStatus("completed");
            rideRequestRepository.save(rideRequest);

            // Save ride history
            RideHistory rideHistory = new RideHistory();
            rideHistory.setCustomer(customer);
            rideHistory.setPickupLatitude(customer.getLatitude());
            rideHistory.setPickupLongitude(customer.getLongitude());
            rideHistory.setDropOffLatitude(rideRequest.getDropOffLatitude());
            rideHistory.setDropOffLongitude(rideRequest.getDropOffLongitude());
            rideHistory.setDistance(distance);
            rideHistory.setTotalAmount(Math.round(totalAmount)); // Rounded total amount
            rideHistory.setServiceName(carService.getName());
            rideHistory.setPrice(Math.round(totalAmount)); // Ensure price is rounded to the nearest whole number
            rideHistory.setVehicleType(rideRequest.getVehicleType());
            rideHistory.setDateCompleted(new Date());

            rideHistoryRepository.save(rideHistory);

            LOGGER.info("Trip ended, ride history saved, and driver status updated successfully for RideRequest ID: {}", rideRequestId);

            // Prepare success response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Trip ended successfully");
            response.put("rideRequestId", rideRequestId);
            response.put("totalAmount", Math.round(totalAmount));
            response.put("distance", distance);
            response.put("status", "completed");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            LOGGER.error("Error ending trip", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            LOGGER.error("Unexpected error ending trip", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An unexpected error occurred\"}");
        }
    }
}
