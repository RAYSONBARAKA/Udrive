package com.example.DriverApp.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DriverApp.DTO.ApiResponse;
import com.example.DriverApp.Entities.CarService;
import com.example.DriverApp.Entities.Customer;
import com.example.DriverApp.Entities.Driver;
import com.example.DriverApp.Entities.RideHistory;
import com.example.DriverApp.Entities.RideRequest;
import com.example.DriverApp.Repositories.DriverRepository;
import com.example.DriverApp.Repositories.RideRequestRepository;
import com.example.DriverApp.Repositories.CarServiceRepository;
import com.example.DriverApp.Repositories.CustomerRepository;
import com.example.DriverApp.Repositories.RideHistoryRepository;

@Service
public class RideService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RideHistoryRepository rideHistoryRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CarServiceRepository carServiceRepository;

    // Method to get all vehicle types with estimated prices for a given service name and drop-off location
    public List<CarServiceResponse> getAllVehicleTypesWithPrices(String serviceName, Long customerId, double dropOffLatitude, double dropOffLongitude) {
        // Fetch car services by the service name
        List<CarService> carServices = carServiceRepository.findByServiceName(serviceName);
        
        // Retrieve customer by customerId with exception handling if not found
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    
        // Customer's pickup location
        double pickupLatitude = customer.getLatitude();
        double pickupLongitude = customer.getLongitude();
    
        // Map each car service to a response with vehicle type and estimated price based on distance
        return carServices.stream()
                .map(carService -> {
                    double distance = calculateDistance(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);
                    double price = distance * carService.getRatePerKm();
                    return new CarServiceResponse(carService.getVehicleType(), price);
                })
                .collect(Collectors.toList());
    }
    
    // DTO class for car service response (vehicle type and calculated price)
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
    
    // Calculate distance between two geographic points using Haversine formula
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

    public List<RideRequest> getPendingRideRequestsForDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        return rideRequestRepository.findByDriverAndStatus(driver, "Pending");
    }

    //accept
    public RideRequest acceptRideRequest(Long driverId, Long rideRequestId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        if (!"Pending".equals(rideRequest.getStatus())) {
            throw new RuntimeException("This ride request cannot be accepted as it is no longer pending.");
        }

        rideRequest.setStatus("Accepted");
        rideRequest.setDriver(driver);

        notificationService.createNotification(
                rideRequest.getCustomer(),
                driver,
                "Your ride request has been accepted by " + driver.getFullName()
        );

        return rideRequestRepository.save(rideRequest);
    }

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

    public void updateDriverLocation(Long driverId, double latitude, double longitude) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setLatitude(latitude);
        driver.setLongitude(longitude);
        driverRepository.save(driver);
    }

    public DriverLocation getDriverLocation(Long rideRequestId) {
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("Ride request not found"));

        Driver driver = rideRequest.getDriver();
        if (driver == null) {
            throw new RuntimeException("Driver not assigned yet.");
        }

        return new DriverLocation(driver.getLatitude(), driver.getLongitude());
    }

    public static class DriverLocation {
        private double latitude;
        private double longitude;

        public DriverLocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }

    public void endTrip(Long rideRequestId) {
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found"));

        if (rideRequest.getCarService() == null) {
            throw new RuntimeException("CarService is not associated with this ride request");
        }

        rideRequest.setStatus("COMPLETED");
        rideRequestRepository.save(rideRequest);
    }
}
