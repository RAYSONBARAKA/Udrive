package com.example.DriverApp.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import static java.lang.Math.*;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.DriverApp.DTO.CarTypeWithPriceDTO;
import com.example.DriverApp.DTO.CustomerInfoDTO;
import com.example.DriverApp.DTO.DriverInfoDTO;
import com.example.DriverApp.DTO.RideEstimateDTO;
import com.example.DriverApp.DTO.RideRequestWithCustomerDTO;
import com.example.DriverApp.Entities.Customer;
import com.example.DriverApp.Entities.Driver;
import com.example.DriverApp.Entities.RideRequest;
import com.example.DriverApp.Entities.ServiceEntity;
import com.example.DriverApp.Repositories.CustomerRepository; 
import com.example.DriverApp.Repositories.DriverRepository;
import com.example.DriverApp.Repositories.RideRequestRepository;
import com.example.DriverApp.Repositories.ServiceRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DriverAvailabilityService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private CustomerRepository customerRepository; 

    

    
    private static final Logger logger = LoggerFactory.getLogger(DriverAvailabilityService.class);

    public List<DriverInfoDTO> getAvailableDriversAroundLocation(String serviceName, double customerLatitude, double customerLongitude, double maxDistanceKm) {
        try {
            ServiceEntity service = serviceRepository.findByServiceName(serviceName);
    
            if (service == null) {
                logger.error("Service not found: " + serviceName);
                throw new RuntimeException("Service not found: " + serviceName);
            }
    
            logger.info("Service found: " + serviceName);
    
            // Fetch only active and available drivers for the given service
            List<Driver> availableDrivers = driverRepository.findByRoleAndAvailableAndActive(service.getServiceName(), true, true);
    
            if (availableDrivers.isEmpty()) {
                logger.warn("No available drivers for service: " + serviceName);
                return new ArrayList<>();
            }
    
            logger.info("Found " + availableDrivers.size() + " available drivers for service: " + serviceName);
    
            // List to hold drivers within the specified distance
            List<DriverInfoDTO> driversAroundLocation = new ArrayList<>();
    
            // Calculate the distance for each driver and add those within maxDistanceKm
            for (Driver driver : availableDrivers) {
                double distance = calculateDistance(customerLatitude, customerLongitude, driver.getLatitude(), driver.getLongitude());
    
                logger.info("Driver: " + driver.getFullName() + " is " + distance + " km away.");
    
                // Only add drivers within the max distance
                if (distance <= maxDistanceKm) {
                    driversAroundLocation.add(new DriverInfoDTO(
                            driver.getId(),
                            driver.getFullName(),
                            driver.getPhoneNumber(),
                            driver.getProfilePictureUrl(),
                            driver.getVehicleType(),
                            driver.getVehicleModel(),
                            driver.getVehicleRegistrationNumber(),
                            driver.getVehicleMake(),
                            driver.getLatitude(),
                            driver.getLongitude()
                    ));
                }
            }
    
            logger.info("Found " + driversAroundLocation.size() + " drivers within the max distance.");
    
            return driversAroundLocation;
        } catch (Exception e) {
            logger.error("Error while fetching available drivers around location: ", e);
            throw new RuntimeException("Error fetching drivers around location.", e);
        }
    }


    
//method calculated 
public ResponseEntity<List<CarTypeWithPriceDTO>> getCarTypesWithPrices(Long customerId, double dropOffLatitude, double dropOffLongitude) {

    try {
        // Fetch customer details for pickup location
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found."));
        double pickupLatitude = customer.getLatitude();
        double pickupLongitude = customer.getLongitude();

        // Calculate distance between pickup and drop-off locations
        double distance = calculateDistance(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);

        // Fetch all available services
        List<ServiceEntity> allServices = serviceRepository.findAll();
        if (allServices.isEmpty()) {
            logger.error("No services found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Create a map to hold car types with their total price (price per km * distance)
        Map<String, Double> carTypesWithPricesMap = new HashMap<>();

        // Iterate through each service to get the vehicle types of available drivers
        for (ServiceEntity service : allServices) {
            logger.info("Fetching drivers for service: " + service.getServiceName());

            // Fetch available drivers for this service
            List<Driver> availableDrivers = driverRepository.findByRoleAndAvailable(service.getServiceName(), true);
            if (availableDrivers.isEmpty()) {
                logger.error("No available drivers found for service: " + service.getServiceName());
                continue; // Skip to the next service if no drivers are available
            }

            // Iterate through drivers and add their vehicle types with prices to the map
            for (Driver driver : availableDrivers) {
                String vehicleType = driver.getVehicleType();
                double pricePerKm = service.getPrice(); // Price per kilometer from the ServiceEntity
                double totalPrice = pricePerKm * distance;

                // Store the vehicle type and total price in the map
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
        logger.error("Error while fetching car types with prices: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}










// Helper class to store driver and its corresponding distance
private static class DriverDistance {
    private final Driver driver;
    private final double distance;

    public DriverDistance(Driver driver, double distance) {
        this.driver = driver;
        this.distance = distance;
    }

    public Driver getDriver() {
        return driver;
    }

    public double getDistance() {
        return distance;
    }
}



    //  method to request a driver and create a ride request
    
    public String requestDriver(Long customerId, Long serviceId, String pickupLocation, String dropOffLocation, double customerLatitude, double customerLongitude) {
        // Get all available drivers
        List<Driver> availableDrivers = driverRepository.findByAvailable(true);
        
        if (availableDrivers.isEmpty()) {
            return "Our Drivers are Occupied.";
        }
    
        // Find the customer by ID
        Customer customer = customerRepository.findById(customerId).orElse(null); 
        if (customer == null) {
            return "Customer not found.";
        }
    
        // Find the service
        Optional<ServiceEntity> optionalService = serviceRepository.findById(serviceId);
        if (optionalService.isEmpty()) {
            return "Service not found.";
        }
    
        // Calculate the closest driver
        Driver closestDriver = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Driver driver : availableDrivers) {
            double distance = calculateDistance(customerLatitude, customerLongitude, driver.getLatitude(), driver.getLongitude());
    
            if (distance < closestDistance) {
                closestDistance = distance;
                closestDriver = driver;
            }
        }
    
        if (closestDriver == null) {
            return "No available drivers nearby.";
        }
    
        // Set the driver to unavailable after request
        closestDriver.setAvailable(false);
        driverRepository.save(closestDriver);  // Save the updated driver status
    
        // Create a new RideRequest
        RideRequest rideRequest = new RideRequest();
        rideRequest.setCustomer(customer);  
        rideRequest.setDriver(closestDriver);  
        rideRequest.setPickupLocation(pickupLocation);  
        rideRequest.setDropOffLocation(dropOffLocation);  
        rideRequest.setRequestTime(LocalDateTime.now());  
        rideRequest.setStatus("Requested");  
        rideRequest.setServiceId(serviceId);  
    
        // Save the ride request to the database
        rideRequestRepository.save(rideRequest); 
    
        return "Driver " + closestDriver.getFullName() + " has been successfully requested.";
    }
    


    // Haversine formula 
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in kilometers 
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c; 
    }




    

    // Method to fetch rides that have been requested for a driver
    public List<RideRequestWithCustomerDTO> getRidesForDriver(Long driverId) {
        List<RideRequest> rideRequests = rideRequestRepository.findByDriverId(driverId);

        return rideRequests.stream()
                .map(ride -> {
                    Customer customer = ride.getCustomer(); // Get customer from the ride request
                    
                    CustomerInfoDTO customerInfo = customer != null 
                        ? new CustomerInfoDTO(
                            customer.getFirstName() + " " + customer.getLastName(),
                            customer.getPhoneNumber()
                          ) 
                        : new CustomerInfoDTO("Unknown", "Unknown");

                    return new RideRequestWithCustomerDTO(
                        ride.getId(), 
                        customerInfo,
                        ride.getPickupLocation(), 
                        ride.getDropOffLocation() 
                    );
                })
                .collect(Collectors.toList());
    }

//method to request a driver 
public String requestSelectedDriver(Long customerId, Long driverId, Long serviceId, String pickupLocation, double dropOffLatitude, double dropOffLongitude) throws Exception {
    // Find the customer by ID
    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new Exception("Customer not found."));

    // Find the driver by ID
    Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new Exception("Driver not found."));

    // Find the service by ID
    ServiceEntity service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new Exception("Service not found."));

    // Check if the driver is available
    if (!driver.isAvailable()) {
        return "Driver is not available.";
    }

    // Set the driver to unavailable after the request
    driver.setAvailable(false);
    driverRepository.save(driver);  // Save the updated driver status

    // Create a new RideRequest
    RideRequest rideRequest = new RideRequest();
    rideRequest.setCustomer(customer);  // Set the Customer entity
    rideRequest.setDriver(driver);  // Set the Driver entity
    rideRequest.setPickupLocation(pickupLocation);  // Set the pickup location
    rideRequest.setDropOffLatitude(dropOffLatitude);  // Set the drop-off latitude
    rideRequest.setDropOffLongitude(dropOffLongitude);  // Set the drop-off longitude
    rideRequest.setRequestTime(LocalDateTime.now());  // Set the request time
    rideRequest.setStatus("Requested");  // Set initial status
    rideRequest.setServiceId(serviceId);  // Set the service ID

    // Save the ride request to the database
    rideRequestRepository.save(rideRequest);

    return "Driver " + driver.getFullName() + " has been successfully requested.";
}




    public String endTrip(Long rideRequestId, Long driverId) throws Exception {
        // Find the ride request by ID
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new Exception("Ride request not found."));
    
        // Find the driver by ID
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new Exception("Driver not found."));
    
        // Check if the driver for this ride request matches the given driver
        if (!rideRequest.getDriver().getId().equals(driverId)) {
            throw new Exception("This driver is not assigned to the ride request.");
        }
    
        // Check if the trip has already ended
        if ("Completed".equals(rideRequest.getStatus())) {
            return "This trip has already ended.";
        }
    
        // Set the ride request status to "Completed" or "Ended"
        rideRequest.setStatus("Completed");
        rideRequest.setEndTime(LocalDateTime.now());  // Set the end time
    
        // Save the updated ride request
        rideRequestRepository.save(rideRequest);
    
        // Set the driver to available again
        driver.setAvailable(true);
        driverRepository.save(driver);
    
        return "Trip successfully ended. Driver is now available.";
    }

    public String cancelRide(Long rideRequestId) {
        Optional<RideRequest> optionalRideRequest = rideRequestRepository.findById(rideRequestId);

        if (optionalRideRequest.isEmpty()) {
            return "Ride request not found.";
        }

        RideRequest rideRequest = optionalRideRequest.get();

        if (!"Requested".equals(rideRequest.getStatus())) {
            return "Ride request cannot be canceled or rejected at this stage.";
        }

        rideRequest.setStatus("Canceled");  // Set the status to canceled
        rideRequestRepository.save(rideRequest);  // Update the database

        return "Ride has been successfully canceled.";
    }

    public String rejectRide(Long rideRequestId) {
        Optional<RideRequest> optionalRideRequest = rideRequestRepository.findById(rideRequestId);

        if (optionalRideRequest.isEmpty()) {
            return "Ride request not found.";
        }

        RideRequest rideRequest = optionalRideRequest.get();

        if (!"Requested".equals(rideRequest.getStatus())) {
            return "Ride request cannot be canceled or rejected at this stage.";
        }

        rideRequest.setStatus("Rejected");  
        rideRequestRepository.save(rideRequest); 

        return "Ride has been successfully rejected.";
    }


    
}
    

