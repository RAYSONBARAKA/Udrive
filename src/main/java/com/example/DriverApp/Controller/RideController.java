package com.example.DriverApp.Controller;

import com.example.DriverApp.DTO.ApiResponse;
import com.example.DriverApp.Entities.RideRequest;
import com.example.DriverApp.Service.RideService;
import com.example.DriverApp.Service.RideService.CarServiceResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/open/rides")
@CrossOrigin(origins = "*")
public class RideController {

    @Autowired
    private RideService rideService;

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



        
}
