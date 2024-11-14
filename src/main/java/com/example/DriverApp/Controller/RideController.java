package com.example.DriverApp.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.DriverApp.DTO.ApiResponse;
import com.example.DriverApp.Entities.RideRequest;
import com.example.DriverApp.Service.RideService;
import com.example.DriverApp.Service.RideService.CarServiceResponse;

@RestController
@RequestMapping("/api/open/rides")
@CrossOrigin(origins = "*")
public class RideController {

    @Autowired
    private RideService rideService;

    // Calculate price endpoint
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

    // Get all vehicle types with prices for a given service name and drop-off location
    @GetMapping("/{id}/{serviceName}/{dropOffLatitude}/{dropOffLongitude}")
    public ResponseEntity<List<CarServiceResponse>> getAllVehicleTypesWithPrices(
            @PathVariable Long id,
            @PathVariable String serviceName,
            @PathVariable double dropOffLatitude,
            @PathVariable double dropOffLongitude) {

        List<CarServiceResponse> carServiceResponses = rideService.getAllVehicleTypesWithPrices(serviceName, id, dropOffLatitude, dropOffLongitude);
        return ResponseEntity.ok(carServiceResponses);  
    }

    // Send ride request to drivers with the same vehicle type
    @PostMapping("/send-request")
    public ResponseEntity<List<RideRequest>> sendRequestToDrivers(
            @RequestParam Long customerId,
            @RequestParam String vehicleType,
            @RequestParam double dropOffLatitude,
            @RequestParam double dropOffLongitude,
            @RequestParam Long serviceId) {  
        
        List<RideRequest> rideRequests = rideService.sendRequestToDriversWithSameVehicleType(customerId, vehicleType, dropOffLatitude, dropOffLongitude, serviceId);
        return ResponseEntity.ok(rideRequests);
    }

    // Get all pending ride requests for a driver
    @GetMapping("/ride-requests/pending")
    public ResponseEntity<List<RideRequest>> getPendingRideRequests(
            @RequestParam Long driverId) {
        
        List<RideRequest> pendingRequests = rideService.getPendingRideRequestsForDriver(driverId);
        return ResponseEntity.ok(pendingRequests);
    }

    @PostMapping("/accept")
    public ResponseEntity<ApiResponse<String>> acceptRideRequest(
            @RequestParam Long driverId,
            @RequestParam Long rideRequestId) {
    
        try {
            rideService.acceptRideRequest(driverId, rideRequestId);
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK, "Success", "Ride request accepted successfully.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            ApiResponse<String> response = new ApiResponse<>(HttpStatus.BAD_REQUEST, null, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
            
    

    // Reject a ride request
    @PostMapping("/ride-requests/reject")
    public ResponseEntity<RideRequest> rejectRideRequest(
            @RequestParam Long driverId, 
            @RequestParam Long rideRequestId) {

        RideRequest rejectedRequest = rideService.rejectRideRequest(driverId, rideRequestId);
        return ResponseEntity.ok(rejectedRequest);
    }

    // End the trip
    @PostMapping("/end/{rideRequestId}")
    public ResponseEntity<String> endTrip(@PathVariable Long rideRequestId) {
        try {
            rideService.endTrip(rideRequestId);
            return ResponseEntity.ok("Trip ended successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error ending trip: " + e.getMessage());
        }
    }

}
