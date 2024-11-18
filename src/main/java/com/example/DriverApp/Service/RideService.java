package com.example.DriverApp.Service;

import io.socket.client.Socket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.example.DriverApp.Entities.*;
import com.example.DriverApp.Repositories.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
 
@Service
public class RideService {

    private final Socket socket;

    @Autowired
    public RideService(Socket socket) {
        this.socket = socket; // Inject the Socket.IO client
    }

    @Autowired
    private NotificationService notificationService;

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

    // Get vehicle types with estimated prices based on distance
    public List<CarServiceResponse> getAllVehicleTypesWithPrices(String serviceName, Long customerId, double dropOffLatitude, double dropOffLongitude) {
        List<CarService> carServices = carServiceRepository.findByServiceName(serviceName);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        double pickupLatitude = customer.getLatitude();
        double pickupLongitude = customer.getLongitude();

        return carServices.stream()
                .map(carService -> {
                    double distance = calculateDistance(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);
                    double price = distance * carService.getRatePerKm();
                    return new CarServiceResponse(carService.getVehicleType(), price);
                })
                .collect(Collectors.toList());
    }

   // Calculate distance using the Haversine formula
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

    // // Accept a ride request
    // public RideRequest acceptRideRequest(Long driverId, Long rideRequestId) {
    //     Driver driver = driverRepository.findById(driverId)
    //             .orElseThrow(() -> new RuntimeException("Driver not found"));

    //     RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
    //             .orElseThrow(() -> new RuntimeException("Ride request not found"));

    //     if (!"Pending".equals(rideRequest.getStatus())) {
    //         throw new RuntimeException("This ride request cannot be accepted as it is no longer pending.");
    //     }

    //     rideRequest.setStatus("Accepted");
    //     rideRequest.setDriver(driver);

    //     String messageBody = "Hello, your ride request has been accepted by "
    //             + driver.getFullName()
    //             + ". Your driver will arrive shortly.";

    //     notificationService.createNotification(rideRequest.getCustomer(), driver, messageBody);

    //     String deviceToken = rideRequest.getCustomer().getToken();

    //     if (socket.connected()) {
    //         socket.emit("rideAccepted", deviceToken, messageBody);
    //         System.out.println("Sent ride acceptance message to server: " + messageBody);
    //     } else {
    //         System.out.println("Socket is not connected. Unable to send notification.");
    //     }

    //     return rideRequestRepository.save(rideRequest);
    // }

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
    
        // Send notification email to the customer
        String customerEmail = rideRequest.getCustomer().getEmail();
        String customerName = rideRequest.getCustomer().getFirstName();
        String driverName = driver.getFullName();
    
         emailService.sendRideAcceptedEmail(customerEmail, customerName, driverName);
    
        return rideRequestRepository.save(rideRequest);
    }
    









    // Other methods like rejectRideRequest, updateDriverLocation, endTrip, etc.
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

    public void endTrip(Long rideRequestId) {
        RideRequest rideRequest = rideRequestRepository.findById(rideRequestId)
                .orElseThrow(() -> new RuntimeException("RideRequest not found"));

        if (rideRequest.getCarService() == null) {
            throw new RuntimeException("CarService is not associated with this ride request");
        }

        rideRequest.setStatus("COMPLETED");
        rideRequestRepository.save(rideRequest);
    }

    public List<RideRequest> getPendingRideRequestsForDriver(Long driverId) {
        // Find the driver by ID
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    
        // Fetch and return all ride requests with "Pending" status for the driver
        return rideRequestRepository.findByDriverAndStatus(driver, "Pending");
    }
    
}
