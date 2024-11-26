package com.example.DriverApp.Service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.DriverApp.DTO.DriverSocketDto;
import com.example.DriverApp.DTO.Message;
import com.example.DriverApp.DTO.PendingRideRequestDTO;
import com.example.DriverApp.DTO.RideHistoryDTO;
import com.example.DriverApp.Entities.*;
import com.example.DriverApp.Repositories.*;
 import com.example.DriverApp.Utility.Mapper;

import io.socket.client.Socket;
 
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
 import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 import java.util.stream.Collectors;


 
@Service
 public class RideService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RideService.class);


    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
private RideHistoryRepository rideHistoryRepository;

 
@Autowired
private ProductsRepository productRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarServiceRepository carServiceRepository;

  

    @Autowired
    Socket socket;
   

    @Autowired
    private DriverDetailsRepository driverDetailsRepository;

    public RideService(RideRequestRepository rideRequestRepository, DriverRepository driverRepository) {
        this.rideRequestRepository = rideRequestRepository;
        this.driverRepository = driverRepository;
    }
 
    // DTO for car service response (vehicle type and calculated price)
    public static class CarServiceResponse {
        private String vehicleType;
        private double estimatedPrice;

        public CarServiceResponse(String vehicleType, double estimatedPrice) {
            this.vehicleType = vehicleType;
            this.estimatedPrice = estimatedPrice;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public double getEstimatedPrice() {
            return estimatedPrice;
        }
    }

    public List<CarServiceResponse> getAllVehicleTypesWithPrices(
        String serviceName,
        Long customerId,
        double dropOffLatitude,
        double dropOffLongitude) {

    // Retrieve car services based on serviceName
    List<CarService> carServices = carServiceRepository.findByServiceName(serviceName);

    // Retrieve the customer based on the customerId
    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

    double pickupLatitude = customer.getLatitude();
    double pickupLongitude = customer.getLongitude();

    // Calculate price for each vehicle type in each car service
    return carServices.stream()
            .map(carService -> {
                double distance = calculateDistance(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);
                long roundedDistance = Math.round(distance); // Round distance to the nearest integer
                
                double price = roundedDistance * carService.getRatePerKm(); // Calculate price
                long roundedPrice = Math.round(price); // Round price to the nearest integer
                
                // Return response with rounded values
                return new CarServiceResponse(
                        carService.getVehicleType(), // Vehicle type
                        roundedPrice                 // Estimated price (without decimals)
                );
            })
            .collect(Collectors.toList());
}

   // Calculate price using the Haversine formula
public long calculatePrice(Long customerId, String serviceName, String vehicleType, double dropOffLatitude, double dropOffLongitude) {
    // Retrieve customer by customerId
    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

    // Calculate distance using customer and drop-off coordinates
    double pickupLatitude = customer.getLatitude();
    double pickupLongitude = customer.getLongitude();
    double distance = calculateDistance(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);

    // Retrieve specific car service by serviceName and vehicleType
    CarService carService = carServiceRepository.findByServiceNameAndVehicleType(serviceName, vehicleType)
            .orElseThrow(() -> new RuntimeException("Car service not available for selected type and service"));

     long price = Math.round(distance * carService.getRatePerKm());  

     return price;
}




 
private long calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    final int EARTH_RADIUS = 6371;  

    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);

    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    double distance = EARTH_RADIUS * c;

     return Math.round(distance);
}




    // Send requests to drivers with the same vehicle type
    public List<RideRequest> sendRequestToDriversWithSameVehicleType(
        Long customerId, String vehicleType, double dropOffLatitude, 
        double dropOffLongitude, Long serviceId) {

    // Fetch customer details
    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

    // Get customer's pickup location
    double pickupLatitude = customer.getLatitude();
    double pickupLongitude = customer.getLongitude();

    // Find drivers matching the vehicle type
    List<Driver> drivers = driverRepository.findByVehicleTypeAndActive(vehicleType, true);
    if (drivers.isEmpty()) {
        throw new RuntimeException("No drivers available for the specified vehicle type: " + vehicleType);
    }

    // Fetch car service details
    CarService carService = carServiceRepository.findById(serviceId)
            .orElseThrow(() -> new RuntimeException("CarService not found with ID: " + serviceId));

    String serviceName = carService.getName();
    if (serviceName == null || serviceName.isEmpty()) {
        throw new RuntimeException("CarService with ID " + serviceId + " has no valid name.");
    }

    // Create and save ride requests for each driver
    List<RideRequest> rideRequests = new ArrayList<>();
    for (Driver driver : drivers) {
        RideRequest rideRequest = new RideRequest();
        rideRequest.setCustomer(customer);
        rideRequest.setDriver(driver);
        rideRequest.setPickupLatitude(pickupLatitude);
        rideRequest.setPickupLongitude(pickupLongitude);
        rideRequest.setDropOffLatitude(dropOffLatitude);
        rideRequest.setDropOffLongitude(dropOffLongitude);
        rideRequest.setStatus("Pending");
        rideRequest.setServiceId(serviceId);
        rideRequest.setCarService(carService);
        rideRequest.setServiceName(serviceName);  
        rideRequest.setVehicleType(vehicleType);
        rideRequest.setDriverName(driver.getFullName());

        // Save the ride request
        rideRequests.add(rideRequestRepository.save(rideRequest));
    }

    return rideRequests;
}

    
    public ResponseEntity<Map<String, Object>> acceptRideRequest(Long driverId, Long rideRequestId) {
        LOGGER.info("Driver {} accepting ride request {}", driverId, rideRequestId);
    
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> {
                    LOGGER.error("Driver with ID {} not found", driverId);
                    return new RuntimeException("Driver not found");
                });
    
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> {
                    LOGGER.error("RideRequest with ID {} not found", rideRequestId);
                    return new RuntimeException("Ride request not found");
                });
    
        if (!"Pending".equals(rideRequest.getStatus())) {
            LOGGER.error("RideRequest {} is not in Pending status", rideRequestId);
            throw new RuntimeException("Ride request is no longer pending");
        }
    
        // Update ride request status and assign driver to the ride request
        rideRequest.setStatus("Accepted");
        rideRequest.setDriver(driver);
        rideRequestRepository.save(rideRequest);  
        LOGGER.info("RideRequest {} status updated to Accepted", rideRequestId);
    
        // Save DriverDetails with the updated rideRequest
        DriverDetails driverDetails = new DriverDetails();
        driverDetails.setDriverId(driver.getId());
        driverDetails.setLongitude(driver.getLongitude());
        driverDetails.setLatitude(driver.getLatitude());
        driverDetails.setProfilePictureUrl(driver.getProfilePictureUrl());
        driverDetails.setPhoneNumber(driver.getPhoneNumber());
        driverDetails.setDriverName(driver.getFullName());
        driverDetails.setCustomerId(rideRequest.getCustomer().getId());
        driverDetails.setVehicleRegistrationNumber(driver.getVehicleRegistrationNumber());
        driverDetails.setVehicleMake(driver.getVehicleMake());
    
         driverDetails.setRideRequest(rideRequest);
    
        driverDetailsRepository.save(driverDetails);  
        LOGGER.info("Driver details for Driver {} saved successfully", driverId);
    
        // Prepare and send Socket.IO message
        DriverSocketDto driverSocketDto = DriverSocketDto.builder()
                .driverName(driver.getFullName())
                .id(driverId)
                .eta("40 mins")
                .build();
    
        Message message = Message.builder()
                .to(String.valueOf(rideRequest.getCustomer().getId()))
                .message(Mapper.classToString(driverSocketDto))
                .build();
    
        try {
            socket.emit("server", Mapper.classToString(message));
            LOGGER.info("Ride accepted notification sent via Socket.IO");
        } catch (Exception e) {
            LOGGER.error("Failed to send ride accepted notification: {}", e.getMessage(), e);
        }
    
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("status", "100 CONTINUE");
        response.put("data", rideRequest);
        response.put("message", "Ride request accepted and driver details stored");
        LOGGER.info("RideRequest {} acceptance process completed successfully", rideRequestId);
    
        return ResponseEntity.ok(response);
    }
    
    //getting the drive details frolm the database 

    public ResponseEntity<Map<String, Object>> getRecentDriverDetailsByCustomerId(Long customerId) {
        LOGGER.info("Fetching the most recent driver detail by ID for customer ID {}", customerId);
    
        // Fetch the most recently entered DriverDetails for the given customer ID where status is "incomplete"
        DriverDetails recentDriverDetails = driverDetailsRepository.findTopByCustomerIdAndStatusOrderByIdDesc(customerId, "incomplete")
                .orElseThrow(() -> {
                    LOGGER.error("No incomplete driver details found for customer ID {}", customerId);
                    return new RuntimeException("No incomplete driver details found for this customer");
                });
    
        // Prepare the response with the most recent record
        Map<String, Object> response = new HashMap<>();
        response.put("status", "200 OK");
        response.put("data", recentDriverDetails); // Only one record
        response.put("message", "Most recent incomplete driver detail fetched successfully");
    
        LOGGER.info("Driver is on his way {}", customerId);
    
        return ResponseEntity.ok(response);
    }
    
    public void updateRideStatus(Long rideRequestId, String status) {
        // Fetch DriverDetails by rideRequestId
        DriverDetails driverDetails = driverDetailsRepository.findByRideRequestId(rideRequestId);
    
        if (driverDetails == null) {
            throw new RuntimeException("Driver details not found for rideRequestId: " + rideRequestId);
        }
    
        // Update status and save
        driverDetails.setStatus(status);
        driverDetailsRepository.save(driverDetails);
    }
    
    public void endTrip(Long rideRequestId) {
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
    
        // Fetch Driver details from CarService
        Driver driver = carService.getDriver();
        if (driver == null) {
            throw new RuntimeException("Driver not associated with the car service");
        }
    
        // Set driver_name in RideRequest
        rideRequest.setDriverName(driver.getFullName());
    
        // Fetch Service Name directly from RideRequest
        String serviceName = rideRequest.getServiceName();
        if (serviceName == null || serviceName.isEmpty()) {
            throw new RuntimeException("Service name is not set for this ride request");
        }
    
        // Calculate distance
        double distance = calculateDistance(
                customer.getLatitude(), customer.getLongitude(),
                rideRequest.getDropOffLatitude(), rideRequest.getDropOffLongitude()
        );
    
        // Calculate total amount
        double totalAmount = distance * carService.getRatePerKm();
    
        // Update RideRequest status to "COMPLETED" and save the updated RideRequest
        rideRequest.setStatus("COMPLETED");
        rideRequestRepository.save(rideRequest);
    
        // Save ride history
        RideHistory rideHistory = new RideHistory();
        rideHistory.setCustomer(customer);
        rideHistory.setCustomerFirstName(customer.getFirstName()); // Save the customer's first name
        rideHistory.setCustomerLastName(customer.getLastName());  // Save the customer's last name
        rideHistory.setPickupLatitude(customer.getLatitude());
        rideHistory.setPickupLongitude(customer.getLongitude());
        rideHistory.setDropOffLatitude(rideRequest.getDropOffLatitude());
        rideHistory.setDropOffLongitude(rideRequest.getDropOffLongitude());
        rideHistory.setDistance(distance);
        rideHistory.setTotalAmount(totalAmount);
        rideHistory.setPrice(totalAmount);
        rideHistory.setServiceName(serviceName);  // Save the fetched service name
        rideHistory.setDriverName(rideRequest.getDriverName());
        rideHistory.setVehicleType(rideRequest.getVehicleType());
        rideHistory.setDateCompleted(new Date());
    
        rideHistoryRepository.save(rideHistory);
    
        LOGGER.info("Trip ended and ride history saved successfully for RideRequest ID: {}", rideRequestId);
    }
    
    

    public List<RideHistoryDTO> getRideHistoryByCustomerId(Long customerId) {
         List<RideHistory> rideHistories = rideHistoryRepository.findByCustomerId(customerId);
    
         return rideHistories.stream()
                .map(rideHistory -> {
                     String customerFirstName = rideHistory.getCustomerFirstName();
                    String customerLastName = rideHistory.getCustomerLastName();
    
                    // Create and return a new RideHistoryDTO with all necessary fields
                    return new RideHistoryDTO(
                            rideHistory.getDistance(),
                            rideHistory.getTotalAmount(),
                            rideHistory.getPrice(),
                            rideHistory.getServiceName(),
                            rideHistory.getVehicleType(),
                            rideHistory.getPickupLatitude(),
                            rideHistory.getPickupLongitude(),
                            rideHistory.getDropOffLatitude(),
                            rideHistory.getDropOffLongitude(),
                            rideHistory.getDriverName(),
                            customerFirstName,  // Add customer first name
                            customerLastName    // Add customer last name
                    );
                })
                .collect(Collectors.toList());
    }
    

    public List<RideHistoryDTO> getAllRideHistories() {
         List<RideHistory> rideHistories = rideHistoryRepository.findAll();
    
         return rideHistories.stream()
                .map(rideHistory -> new RideHistoryDTO(
                        rideHistory.getDistance(),
                        rideHistory.getTotalAmount(),
                        rideHistory.getPrice(),
                        rideHistory.getServiceName(),
                        rideHistory.getVehicleType(),
                        rideHistory.getPickupLatitude(),
                        rideHistory.getPickupLongitude(),
                        rideHistory.getDropOffLatitude(),
                        rideHistory.getDropOffLongitude(),
                        rideHistory.getDriverName(),
                        rideHistory.getCustomerFirstName(),  
                        rideHistory.getCustomerLastName()    
                ))
                .collect(Collectors.toList());
    }
    

    
    


    // Get recent notification for a customer
    public ResponseEntity<Map<String, Object>> getRecentNotification(Long customerId) {
        Notification recentNotification = notificationRepository
                .findTopByCustomerIdOrderByDateDesc(customerId)
                .orElseThrow(() -> new RuntimeException("No notifications found for customer ID: " + customerId));

        Map<String, Object> response = new HashMap<>();
        response.put("status", "200 OK");
        response.put("message", "Recent notification retrieved successfully");
        response.put("data", recentNotification);

        return ResponseEntity.ok(response);
    }

    // Reject a ride request
    public RideRequest rejectRideRequest(Long driverId, Long rideRequestId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (!"Pending".equals(rideRequest.getStatus())) {
            throw new RuntimeException("Ride request is no longer pending");
        }

        rideRequest.setStatus("Rejected");
        return rideRequestRepository.save(rideRequest);
    }



 

    public List<PendingRideRequestDTO> getPendingRideRequestsByServiceId(Long serviceId) {
        LOGGER.info("Fetching all pending ride requests for Service ID: {}", serviceId);
    
        // Fetch pending ride requests
        List<RideRequest> rideRequests = rideRequestRepository.findPendingRidesByServiceId("Pending", serviceId);
    
        if (rideRequests.isEmpty()) {
            LOGGER.warn("No pending ride requests found for Service ID: {}", serviceId);
        } else {
            LOGGER.info("Found {} pending ride requests for Service ID: {}", rideRequests.size(), serviceId);
        }
    
        // Convert to DTO
        return rideRequests.stream()
                .map(rideRequest -> new PendingRideRequestDTO(
                        rideRequest.getId(),
                        rideRequest.getCustomer().getFirstName(),
                        rideRequest.getCustomer().getLastName(),
                        rideRequest.getCustomer().getLatitude(),
                        rideRequest.getCustomer().getLongitude(),
                        rideRequest.getCustomer().getPhoneNumber()
                ))
                .toList();
    }
    // Get ride request by ID
    public RideRequest getRideRequestById(Long rideRequestId) {
        return rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found with ID: " + rideRequestId));
    }


    
 }    