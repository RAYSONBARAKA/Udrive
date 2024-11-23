package com.example.DriverApp.Controller;

import com.example.DriverApp.DTO.ApiResponse;
import com.example.DriverApp.DTO.PendingRideRequestDTO;
import com.example.DriverApp.Entities.ArchiveNotification;
import com.example.DriverApp.Entities.Driver;
import com.example.DriverApp.Entities.DriverDetails;
import com.example.DriverApp.Entities.Notification;
import com.example.DriverApp.Entities.RideRequest;
import com.example.DriverApp.Service.RideService;
import com.example.DriverApp.Service.RideService.CarServiceResponse;
import com.example.DriverApp.Repositories.ArchiveNotificationRepository;
import com.example.DriverApp.Repositories.DriverDetailsRepository;
import com.example.DriverApp.Repositories.NotificationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

   @Autowired
    private ArchiveNotificationRepository archiveNotificationRepository;

        private static final Logger LOGGER = LoggerFactory.getLogger(RideController.class);

 
     

    @Autowired
    private NotificationRepository notificationRepository; // Inject the NotificationRepository

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
    public ResponseEntity<List<CarServiceResponse>> getAllVehicleTypesWithPrices(
            @PathVariable Long id,
            @PathVariable String serviceName,
            @PathVariable double dropOffLatitude,
            @PathVariable double dropOffLongitude) {

        List<CarServiceResponse> carServiceResponses = rideService.getAllVehicleTypesWithPrices(
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

    // Endpoint to end a trip
    @PostMapping("/end/{rideRequestId}")
    public ResponseEntity<String> endTrip(@PathVariable Long rideRequestId) {
        try {
            rideService.endTrip(rideRequestId);
            return ResponseEntity.ok("Trip ended successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error ending trip: " + e.getMessage());
        }
    }


     
    @GetMapping("/recent-notification/{customerId}")
    public ResponseEntity<Map<String, Object>> getRecentNotification(@PathVariable Long customerId) {
        // Retrieve the most recent notification for the customer
        Notification recentNotification = notificationRepository
                .findTopByCustomerIdOrderByDateDesc(customerId)
                .orElseThrow(() -> new RuntimeException("No notifications found for customer ID: " + customerId));

        // Move the notification to the archive
        ArchiveNotification archiveNotification = new ArchiveNotification(
            recentNotification.getId(),
            recentNotification.getCustomer(),
            recentNotification.getDriverId(),
            recentNotification.getMessage(),
            recentNotification.getSubject(),
            recentNotification.getDate(),
            recentNotification.getCreatedAt(),
            recentNotification.getStatus(),
            recentNotification.getRecipientEmail()
        );

        // Save the archived notification
        archiveNotificationRepository.save(archiveNotification);

        // Optionally, delete the notification from the active table
        notificationRepository.delete(recentNotification);

        // Prepare the response map
        Map<String, Object> response = new HashMap<>();
        response.put("status", "200 OK");
        response.put("message", "Recent notification archived and retrieved successfully");
        response.put("data", recentNotification.getMessage()); // Return only the message

        // Return the response as a ResponseEntity
        return ResponseEntity.ok(response);
    }
}

