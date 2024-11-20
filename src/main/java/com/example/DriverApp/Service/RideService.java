package com.example.DriverApp.Service;

import io.socket.client.Socket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.example.DriverApp.Entities.*;
import com.example.DriverApp.Repositories.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import java.time.LocalDateTime;

@Service
public class RideService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarServiceRepository carServiceRepository;

    @Autowired
    private JavaMailSender mailSender;

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
    Optional<CarService> carServices = carServiceRepository.findByServiceName(serviceName);

    // Retrieve the customer based on the customerId
    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

    double pickupLatitude = customer.getLatitude();
    double pickupLongitude = customer.getLongitude();

    // Calculate price for each vehicle type in each car service
    return carServices.stream()
            .map(carService -> {
                double distance = calculateDistance(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);
                double price = distance * carService.getRatePerKm(); // Calculate price based on distance and rate per km
                // Since vehicleType is a String, we pass it directly as the vehicle type
                return new CarServiceResponse(
                        carService.getVehicleType(), // Vehicle type
                        price                        // Estimated price
                );
            })
            .collect(Collectors.toList());
}


    // Calculate price using the Haversine formula
    public double calculatePrice(Long customerId, String serviceName, String vehicleType, double dropOffLatitude, double dropOffLongitude) {
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

        // Return the calculated price
        return distance * carService.getRatePerKm();
    }

    // Haversine formula for distance calculation
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Earth radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    // Send requests to drivers with the same vehicle type
    public List<RideRequest> sendRequestToDriversWithSameVehicleType(Long customerId, String vehicleType, double dropOffLatitude, double dropOffLongitude, Long serviceId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        double pickupLatitude = customer.getLatitude();
        double pickupLongitude = customer.getLongitude();

        List<Driver> drivers = driverRepository.findByVehicleTypeAndActive(vehicleType, true);
        CarService carService = carServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("CarService not found with ID: " + serviceId));

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
            rideRequest.setServiceName(carService.getName());
            rideRequest.setVehicleType(vehicleType);
            rideRequest.setDriverName(driver.getFullName());

            rideRequests.add(rideRequestRepository.save(rideRequest));
        }
        return rideRequests;
    }

    // Accept a ride request
    public ResponseEntity<Map<String, Object>> acceptRideRequest(Long driverId, Long rideRequestId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (!"Pending".equals(rideRequest.getStatus())) {
            throw new RuntimeException("This ride request cannot be accepted as it is no longer pending.");
        }

        rideRequest.setStatus("Accepted");
        rideRequest.setDriver(driver);

        // Prepare the notification message
        String customerEmail = rideRequest.getCustomer().getEmail();
        String customerName = rideRequest.getCustomer().getFirstName();
        String driverName = driver.getFullName();
        String notificationMessage = "Your ride request has been accepted by " + driverName + ".";

        // Send notification email to the customer
        emailService.sendRideAcceptedEmail(customerEmail, customerName, driverName);

        // Create and save the notification in the database
        Notification notification = new Notification(notificationMessage, customerEmail, LocalDateTime.now());
        notificationRepository.save(notification);

        // Save the updated ride request
        rideRequestRepository.save(rideRequest);

        // Prepare custom response
        Map<String, Object> response = new HashMap<>();
        response.put("status", "100 CONTINUE");
        response.put("data", rideRequest);
        response.put("message", notificationMessage);

        return ResponseEntity.ok(response);
    }

    // Get notification by ID
    public Notification getNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
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

    // Update driver location
    public void updateDriverLocation(Long driverId, double latitude, double longitude) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setLatitude(latitude);
        driver.setLongitude(longitude);
        driverRepository.save(driver);
    }

    // End the trip
    public void endTrip(Long rideRequestId) {
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found"));

        if (rideRequest.getCarService() == null) {
            throw new RuntimeException("CarService is not associated with this ride request");
        }

        rideRequest.setStatus("COMPLETED");
        rideRequestRepository.save(rideRequest);
    }

    // Get pending ride requests for a driver
    public List<RideRequest> getPendingRideRequestsForDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        return rideRequestRepository.findByDriverAndStatus(driver, "Pending");
    }

    // Get ride request by ID
    public RideRequest getRideRequestById(Long rideRequestId) {
        return rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found with ID: " + rideRequestId));
    }
}