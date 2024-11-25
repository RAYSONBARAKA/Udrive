package com.example.DriverApp.Controller;

import com.example.DriverApp.DTO.ApiResponse;
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

    @Autowired
    private ProductsRepository productsRepository;

    

    private final RideRequestRepository rideRequestRepository;
    private final RideHistoryRepository rideHistoryRepository;
 
    private static final Logger LOGGER = LoggerFactory.getLogger(RideController.class);

    public RideController(RideRequestRepository rideRequestRepository, 
                          RideHistoryRepository rideHistoryRepository, 
                          CarServiceRepository carServiceRepository) {
        this.rideRequestRepository = rideRequestRepository;
        this.rideHistoryRepository = rideHistoryRepository;
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
//this is the methos of ending ride 
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

        // Fetch Service Name from Product Table using service_id
        Long serviceId = rideRequest.getServiceId(); // Assuming RideRequest has a serviceId field
        Products product = productsRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Product not found for the given service ID"));
        String serviceName = product.getName(); // Assuming Product has a name field representing the service name

        // Calculate distance
        double distance = calculateDistance(
                customer.getLatitude(), customer.getLongitude(),
                rideRequest.getDropOffLatitude(), rideRequest.getDropOffLongitude()
        );
        distance = Math.round(distance);  // Round the distance to the nearest integer

        // Calculate total amount
        double totalAmount = distance * carService.getRatePerKm();  // Total amount based on distance and rate per km
        totalAmount = Math.round(totalAmount);  // Round the total amount to the nearest integer

        // Update RideRequest status to "COMPLETED"
        rideRequest.setStatus("COMPLETED");
        rideRequestRepository.save(rideRequest);

        // Save ride history
        RideHistory rideHistory = new RideHistory();
        rideHistory.setCustomer(customer);
        rideHistory.setPickupLatitude(customer.getLatitude());
        rideHistory.setPickupLongitude(customer.getLongitude());
        rideHistory.setDropOffLatitude(rideRequest.getDropOffLatitude());
        rideHistory.setDropOffLongitude(rideRequest.getDropOffLongitude());
        rideHistory.setDistance(distance);
        rideHistory.setDriverName(rideRequest.getDriverName());
        rideHistory.setTotalAmount(totalAmount);   
        rideHistory.setPrice(totalAmount);        
        rideHistory.setServiceName(serviceName);  // Use fetched service name from Product table
        rideHistory.setVehicleType(rideRequest.getVehicleType());
        rideHistory.setDateCompleted(new Date());

        rideHistoryRepository.save(rideHistory);

        // Create response object to send back as JSON
        RideResponse rideResponse = new RideResponse();
        rideResponse.setStatus("COMPLETED");
        rideResponse.setMessage("Trip ended successfully");
        rideResponse.setTotalAmount(totalAmount);

        return ResponseEntity.ok(rideResponse);
    } catch (Exception e) {
        LOGGER.error("Error ending trip", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to end trip: " + e.getMessage());
    }
}


    @GetMapping("/get-ridehistory/all")
    public ResponseEntity<Object> getAllRideHistories() {
        try {
            List<RideHistoryDTO> allRideHistories = rideService.getAllRideHistories();

            if (allRideHistories.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No ride history found");
            }

            return ResponseEntity.ok(allRideHistories);
        } catch (Exception e) {
            LOGGER.error("Error fetching all ride histories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch ride histories: " + e.getMessage());
        }
    }

   @GetMapping("/byCustomer/history/{customerId}")
    public ResponseEntity<Object> getRideHistoryByCustomer(@PathVariable Long customerId) {
        try {
            List<RideHistoryDTO> rideHistoryDTOs = rideService.getRideHistoryByCustomerId(customerId);

            if (rideHistoryDTOs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No ride history found for customer ID: " + customerId);
            }

            return ResponseEntity.ok(rideHistoryDTOs);
        } catch (Exception e) {
            LOGGER.error("Error fetching ride history for customer ID: {}", customerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to fetch ride history: " + e.getMessage());
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
}
