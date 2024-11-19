package com.example.DriverApp.Controller;

import com.example.DriverApp.DTO.ApiResponse;
import com.example.DriverApp.Entities.Notification;
import com.example.DriverApp.Entities.RideRequest;
import com.example.DriverApp.Service.RideService;
import com.example.DriverApp.Service.RideService.CarServiceResponse;
 import com.example.DriverApp.Repositories.NotificationRepository;

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

    // Endpoint to get pending ride requests for a driver
    @GetMapping("/ride-requests/pending")
    public ResponseEntity<List<RideRequest>> getPendingRideRequests(
            @RequestParam Long driverId) {

        List<RideRequest> pendingRequests = rideService.getPendingRideRequestsForDriver(driverId);
        return ResponseEntity.ok(pendingRequests);
    }

    // Endpoint for a driver to accept a ride request
    @PostMapping("/accept")
    public ResponseEntity<Map<String, Object>> acceptRideRequest(
            @RequestParam Long driverId,
            @RequestParam Long rideRequestId) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Accept the ride request using RideService
            rideService.acceptRideRequest(driverId, rideRequestId);

            // Retrieve the RideRequest using RideService (assuming this method exists in RideService)
            RideRequest rideRequest = rideService.getRideRequestById(rideRequestId);

            // Create and save the notification
            String customerEmail = rideRequest.getCustomer().getEmail();
            String customerName = rideRequest.getCustomer().getFirstName();
            String driverName = rideRequest.getDriver().getFullName();

            Notification notification = new Notification();
            notification.setRecipientEmail(customerEmail);
            notification.setMessage("Your ride request has been accepted by " + driverName);
            notification.setSubject("Ride Accepted");
            notificationRepository.save(notification);

            response.put("status", "100 CONTINUE");
            response.put("data", null); 
            response.put("message", "Ride request accepted successfully.");
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


     
    
   
@GetMapping("/recent-notification")
public ResponseEntity<Map<String, Object>> getRecentNotification(@RequestParam Long customerId) {
    // Retrieve the most recent notification for the customer, ordered by 'date'
    Notification recentNotification = notificationRepository
            .findTopByCustomerIdOrderByDateDesc(customerId)
            .orElseThrow(() -> new RuntimeException("No notifications found for customer ID: " + customerId));

    // Prepare the response map
    Map<String, Object> response = new HashMap<>();
    response.put("status", "200 OK");
    response.put("message", "Recent notification retrieved successfully");
    response.put("data", recentNotification.getMessage()); // Return only the message

    // Return the response as a ResponseEntity
    return ResponseEntity.ok(response);
}
 
}


