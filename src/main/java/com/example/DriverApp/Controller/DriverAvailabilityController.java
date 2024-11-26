package com.example.DriverApp.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.DriverApp.DTO.CarTypeWithPriceDTO;
import com.example.DriverApp.DTO.DriverInfoDTO;
import com.example.DriverApp.DTO.RideRequestWithCustomerDTO;
import com.example.DriverApp.DTO.RidesResponse;
import com.example.DriverApp.Entities.Customer;
import com.example.DriverApp.Entities.Driver;
import com.example.DriverApp.Entities.ServiceEntity;
import com.example.DriverApp.Repositories.CustomerRepository;
import com.example.DriverApp.Repositories.DriverRepository;
import com.example.DriverApp.Repositories.ServiceRepository;
import com.example.DriverApp.Service.DriverAvailabilityService;

@RestController
@RequestMapping("/api/open/drivers")
@CrossOrigin(origins = "*")

public class DriverAvailabilityController {


 
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    

    @Autowired
    private DriverAvailabilityService driverAvailabilityService; 
    
    @Autowired
    private CustomerRepository customerRepository;




    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; 

        // Convert degrees to radians
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // Haversine formula
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance in km
        double distance = R * c;

        return distance;
    }


    // Fetch available drivers for the specified service
    @GetMapping("/by-service/{id}")
    public ResponseEntity<List<DriverInfoDTO>> getAvailableDriversByService(
            @PathVariable String id,
            @RequestParam String serviceName,
            @RequestParam(defaultValue = "10") double maxDistanceKm) { 
        
        try {
            String cleanedId = id.replaceAll("[{}]", "");
            Long customerId = Long.valueOf(cleanedId);
    
            // Fetch the customer's latitude and longitude from the database
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found."));
    
            double customerLatitude = customer.getLatitude();
            double customerLongitude = customer.getLongitude();
    
            // Fetch available drivers within the specified distance
            List<DriverInfoDTO> driversAroundLocation = driverAvailabilityService.getAvailableDriversAroundLocation(
                    serviceName, customerLatitude, customerLongitude, maxDistanceKm);
    
            // Check if any drivers were found and return appropriate response
            if (driversAroundLocation.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
    
            return ResponseEntity.ok(driversAroundLocation); // Return the list of drivers within range
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    
    // Fetch ride requests associated with a specific driver
    @GetMapping("/{id}/rides")
    public ResponseEntity<RidesResponse> getRequestedRides(@PathVariable Long id) {
        try {
            List<RideRequestWithCustomerDTO> rides = driverAvailabilityService.getRidesForDriver(id);
            if (rides.isEmpty()) {
                return ResponseEntity.ok(new RidesResponse("No rides found for this driver.", rides));
            }
            return ResponseEntity.ok(new RidesResponse("Rides retrieved successfully.", rides));
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
            return ResponseEntity.status(500).body(new RidesResponse("An error occurred while retrieving rides.", null));
        }
    }

    // Request a driver
    @PostMapping("/request-driver")
    public ResponseEntity<RidesResponse> requestSelectedDriver(
            @RequestParam Long customerId,         
            @RequestParam Long driverId,           
            @RequestParam Long serviceId,          
            @RequestParam double dropOffLatitude,  
            @RequestParam double dropOffLongitude  
    ) {
        try {
            //kupata customer 
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new Exception("Customer not found."));
    
            // Get the customer's pickup location as latitude and longitude
            double customerLatitude = customer.getLatitude();   
            double customerLongitude = customer.getLongitude(); 
    
            // Convert latitude and longitude into a string for the pickup location
            String pickupLocation = customerLatitude + ", " + customerLongitude;
    
            // Call the service method with drop-off coordinates
            String responseMessage = driverAvailabilityService.requestSelectedDriver(
                    customerId, driverId, serviceId, pickupLocation, dropOffLatitude, dropOffLongitude);
    
            return ResponseEntity.ok(new RidesResponse(responseMessage, null));  // Return success message in RidesResponse
        } catch (Exception e) {
            e.printStackTrace();  
            return ResponseEntity.status(500).body(new RidesResponse("An error occurred while requesting the driver.", null));
        }
    }
    

    @PostMapping("/end-trip")
    public ResponseEntity<RidesResponse> endTrip(
            @RequestParam Long rideRequestId,    // The ID of the ride request
            @RequestParam Long driverId) {    
        try {
            // Use the driverAvailabilityService to end the trip
            String responseMessage = driverAvailabilityService.endTrip(rideRequestId, driverId);
            
            return ResponseEntity.ok(new RidesResponse(responseMessage, null));  // Return success message in RidesResponse
        } catch (Exception e) {
            e.printStackTrace();  // Log the error for debugging
            return ResponseEntity.status(500).body(new RidesResponse("An error occurred while ending the trip.", null));
        }
    }



    @PostMapping("/{rideRequestId}/cancel")
    public ResponseEntity<RidesResponse> cancelRide(@PathVariable Long rideRequestId) {
        try {
            String responseMessage = driverAvailabilityService.cancelRide(rideRequestId);
            return ResponseEntity.ok(new RidesResponse(responseMessage, null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new RidesResponse("An error occurred while canceling the ride.", null));
        }
    }

    @PostMapping("/{rideRequestId}/reject")
    public ResponseEntity<RidesResponse> rejectRide(@PathVariable Long rideRequestId) {
        try {
            String responseMessage = driverAvailabilityService.rejectRide(rideRequestId);
            return ResponseEntity.ok(new RidesResponse(responseMessage, null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new RidesResponse("An error occurred while rejecting the ride.", null));
        }
    }


    @GetMapping("/estimate")
    public ResponseEntity<List<CarTypeWithPriceDTO>> getCarTypesWithPrices(
            @RequestParam Long customerId,
            @RequestParam double dropOffLatitude,
            @RequestParam double dropOffLongitude) {
    
        try {
            // Fetch customer details for pickup location
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found."));
    
            double pickupLatitude = customer.getLatitude();
            double pickupLongitude = customer.getLongitude();
    
            // Calculate the distance between the pickup and drop-off locations
            double distance = calculateDistance(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);
    
            // Fetch all available services
            List<ServiceEntity> allServices = serviceRepository.findAll();
            if (allServices.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
    
            // Create a map to hold car types with their total price (price per km * distance)
            Map<String, Double> carTypesWithPricesMap = new HashMap<>();
    
            // Iterate through each service to get the vehicle types of available drivers
            for (ServiceEntity service : allServices) {
                // Fetch available drivers for this service
                List<Driver> availableDrivers = driverRepository.findByRoleAndAvailable(service.getServiceName(), true);
    
                if (availableDrivers.isEmpty()) {
                    continue; // Skip to the next service if no drivers are available
                }
    
                // Iterate through drivers and add their vehicle types with total prices to the map
                for (Driver driver : availableDrivers) {
                    String vehicleType = driver.getVehicleType();
                    double pricePerKm = service.getPrice(); // Price per kilometer from the ServiceEntity
                    double totalPrice = pricePerKm * distance;
    
                    // Store the vehicle type and total price in the map (only if the vehicle type is unique)
                    carTypesWithPricesMap.putIfAbsent(vehicleType, totalPrice);
                }
            }
    
            // Convert the map to a list of CarTypeWithPriceDTO
            List<CarTypeWithPriceDTO> carTypesWithPrices = new ArrayList<>();
            for (Map.Entry<String, Double> entry : carTypesWithPricesMap.entrySet()) {
                CarTypeWithPriceDTO dto = new CarTypeWithPriceDTO(entry.getKey(), entry.getValue());
                carTypesWithPrices.add(dto);
            }
    
            // Return the list of car types with their total prices
            return ResponseEntity.ok(carTypesWithPrices);
    
        } catch (Exception e) {
            e.printStackTrace();
            // Return an internal server error if something goes wrong
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
   
}