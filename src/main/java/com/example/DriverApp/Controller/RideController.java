package com.example.DriverApp.Controller;

import com.example.DriverApp.DTO.ApiResponse;
import com.example.DriverApp.DTO.CarServiceResponse;
import com.example.DriverApp.DTO.PendingRideRequestDTO;
import com.example.DriverApp.DTO.RideHistoryDTO;
import com.example.DriverApp.DTO.RideResponse;
import com.example.DriverApp.Entities.*;
import com.example.DriverApp.Service.RideService;
import com.example.DriverApp.Repositories.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/open/rides")
@CrossOrigin(origins = "*")
public class RideController {

    @Autowired
    private RideService rideService;

    @Autowired
    private DriverDetailsRepository driverDetailsRepository;

 

   

    private final RideRequestRepository rideRequestRepository;
    private final RideHistoryRepository rideHistoryRepository;
    private final CarServiceRepository carServiceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(RideController.class);

    public RideController(RideRequestRepository rideRequestRepository, 
                          RideHistoryRepository rideHistoryRepository, 
                          CarServiceRepository carServiceRepository) {
        this.rideRequestRepository = rideRequestRepository;
        this.rideHistoryRepository = rideHistoryRepository;
        this.carServiceRepository = carServiceRepository;
    }

    // Endpoint to calculate the price of a ride
    @GetMapping("/calculate-price")
    public ResponseEntity<Double> calculatePrice(
            @RequestParam Long customerId,
            @RequestParam String serviceName,
            @RequestParam String vehicleType,
            @RequestParam double dropOffLatitude,
            @RequestParam double dropOffLongitude) {

        double price = rideService.calculatePrice(customerId, serviceName, vehicleType, dropOffLatitude, dropOffLongitude);
        return ResponseEntity.ok(price);
    }

    // Endpoint to get all vehicle types with prices for a given service name and location
    @GetMapping("/{id}/{serviceName}/{dropOffLatitude}/{dropOffLongitude}")
    public ResponseEntity<List<com.example.DriverApp.Service.RideService.CarServiceResponse>> getAllVehicleTypesWithPrices(
            @PathVariable Long id,
            @PathVariable String serviceName,
            @PathVariable double dropOffLatitude,
            @PathVariable double dropOffLongitude) {

        List<com.example.DriverApp.Service.RideService.CarServiceResponse> carServiceResponses = rideService.getAllVehicleTypesWithPrices(
                serviceName, id, dropOffLatitude, dropOffLongitude);
        return ResponseEntity.ok(carServiceResponses);
    }

    // Endpoint to send ride requests to drivers
    @PostMapping("/send-request")
    public ResponseEntity<List<RideRequest>> sendRequestToDrivers(
            @RequestParam Long customerId,
            @RequestParam String vehicleType,
            @RequestParam double dropOffLatitude,
            @RequestParam double dropOffLongitude,
            @RequestParam Long serviceId) {

        List<RideRequest> rideRequests = rideService.sendRequestToDriversWithSameVehicleType(
                customerId, vehicleType, dropOffLatitude, dropOffLongitude, serviceId);
        return ResponseEntity.ok(rideRequests);
    }

    @GetMapping("/ride-requests/pending-by-service")
    public ResponseEntity<List<PendingRideRequestDTO>> getPendingRideRequestsByServiceId(
            @RequestParam Long serviceId) {
    
        LOGGER.info("Received request to fetch pending ride requests for Service ID: {}", serviceId);
    
        List<PendingRideRequestDTO> pendingRequests = rideService.getPendingRideRequestsByServiceId(serviceId);
    
        return ResponseEntity.ok(pendingRequests);
    }

    @PostMapping("/accept")
    public ResponseEntity<Map<String, Object>> acceptRideRequest(
            @RequestParam Long driverId,
            @RequestParam Long rideRequestId) {
    
        Map<String, Object> response = new HashMap<>();
    
        try {
            // Check if the driver has already accepted this ride request
            boolean exists = driverDetailsRepository.existsByDriverIdAndCustomerId(driverId, rideRequestId);
            if (exists) {
                response.put("status", "409 CONFLICT");
                response.put("data", null);
                response.put("message", "Driver has already accepted this ride request.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
    
            // Accept the ride request
            rideService.acceptRideRequest(driverId, rideRequestId);
    
            // Fetch ride request and driver details
            RideRequest rideRequest = rideService.getRideRequestById(rideRequestId);
            Driver driver = rideRequest.getDriver();
    
            // Populate DriverDetails
            DriverDetails driverDetails = new DriverDetails();
            driverDetails.setDriverId(driver.getId());
            driverDetails.setDriverName(driver.getFullName());
            driverDetails.setLongitude(driver.getLongitude());
            driverDetails.setLatitude(driver.getLatitude());
            driverDetails.setProfilePictureUrl(driver.getProfilePictureUrl());
            driverDetails.setPhoneNumber(driver.getPhoneNumber());
            driverDetails.setCustomerId(rideRequest.getCustomer().getId());
            driverDetails.setVehicleRegistrationNumber(driver.getVehicleRegistrationNumber());
            driverDetails.setVehicleMake(driver.getVehicleMake());
            driverDetails.setFullName(driver.getFullName());
            driverDetails.setStatus("incomplete");
    
            // Save the details
            driverDetailsRepository.save(driverDetails);
    
            response.put("status", "100 CONTINUE");
            response.put("data", null);
            response.put("message", "Ride request accepted and driver details saved successfully.");
    
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "400 BAD REQUEST");
            response.put("data", null);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/end-trip/markComplete")
    public ResponseEntity<Map<String, Object>> markRideAsComplete(@RequestParam Long rideRequestId) {
        return updateRideStatus(rideRequestId, "complete", "Ride marked as complete successfully.");
    }

    private ResponseEntity<Map<String, Object>> updateRideStatus(Long rideRequestId, String status, String successMessage) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Call service method to update status
            rideService.updateRideStatus(rideRequestId, status);

            // Prepare response
            response.put("status", "200 CONTINUE");
            response.put("data", null);
            response.put("message", successMessage);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "400 BAD REQUEST");
            response.put("data", null);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

     
    // Endpoint for a driver to reject a ride request
    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<String>> rejectRideRequest(
            @RequestParam Long driverId,
            @RequestParam Long rideRequestId) {

        try {
            rideService.rejectRideRequest(driverId, rideRequestId);
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK, "Success", "Ride request rejected successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.BAD_REQUEST, null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

     @PostMapping("/endTrip/{rideRequestId}")
    public ResponseEntity<Object> endTrip(@PathVariable Long rideRequestId) {
        try {
            // Fetch RideRequest
            RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                    .orElseThrow(() -> new RuntimeException("RideRequest not found"));
    
            // Ensure the ride request has an associated car service
            CarService carService = rideRequest.getCarService();
            if (carService == null) {
                throw new RuntimeException("CarService is not associated with this ride request");
            }
    
            // Fetch Customer details
            Customer customer = rideRequest.getCustomer();
            if (customer == null) {
                throw new RuntimeException("Customer not associated with the ride request");
            }
    
            // Fetch DriverDetails and update status
            DriverDetails driverDetails = rideRequest.getDriverDetails();
            if (driverDetails == null) {
                throw new RuntimeException("Driver details not associated with the ride request");
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
    
            // Round totalAmount to the nearest whole number
            long roundedTotalAmount = Math.round(totalAmount);
    
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
            rideHistory.setTotalAmount(roundedTotalAmount);
            rideHistory.setPrice(roundedTotalAmount);
            rideHistory.setServiceName(carService.getName());
            rideHistory.setVehicleType(rideRequest.getVehicleType());
            rideHistory.setDateCompleted(new Date());
    
            rideHistoryRepository.save(rideHistory);
    
            // Create response object to send back as JSON
            RideResponse rideResponse = new RideResponse();
            rideResponse.setStatus("completed");
            rideResponse.setMessage("Trip ended successfully");
            rideResponse.setTotalAmount(roundedTotalAmount);
    
            return ResponseEntity.ok(rideResponse);
        } catch (Exception e) {
            LOGGER.error("Error ending trip", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to end trip: " + e.getMessage());
        }
    }
    
    
    // Helper method to calculate distance between two coordinates
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<RideHistoryDTO>> getRideHistoryByCustomerId(@PathVariable Long customerId) {
        List<RideHistoryDTO> rideHistories = rideService.getRideHistoryByCustomerId(customerId);
        if (rideHistories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rideHistories);
    }
    
    @GetMapping("/ride-history")
    public ResponseEntity<Map<String, Object>> getAllRideHistory() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch all ride history records from the service
            List<RideHistoryDTO> rideHistories = rideService.getAllRideHistory();

            // Check if the list is empty
            if (rideHistories.isEmpty()) {
                response.put("status", "404 NOT FOUND");
                response.put("message", "No ride history records found.");
                return ResponseEntity.status(404).body(response);
            }

            // Return the ride history data in the response
            response.put("status", "200 OK");
            response.put("data", rideHistories);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "500 INTERNAL SERVER ERROR");
            response.put("message", "An error occurred while fetching the ride history.");
            return ResponseEntity.status(500).body(response);
        }


        

 
        
    }
}

