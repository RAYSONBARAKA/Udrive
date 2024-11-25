package com.example.DriverApp.Controller;

import com.example.DriverApp.Entities.RideRequest;
import com.example.DriverApp.Entities.RideHistory;
import com.example.DriverApp.Entities.DriverDetails;
import com.example.DriverApp.Entities.CarService;
import com.example.DriverApp.Repositories.RideRequestRepository;
import com.example.DriverApp.Repositories.RideHistoryRepository;
import com.example.DriverApp.Repositories.DriverDetailsRepository;
import com.example.DriverApp.Repositories.CarServiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/open/driver")
public class TripController {

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private RideHistoryRepository rideHistoryRepository;

    @Autowired
    private DriverDetailsRepository driverDetailsRepository;
 

    // Endpoint to end the trip
    @PostMapping("/end-trip/{requestId}/{driverId}/{customerId}")
    public ResponseEntity<Map<String, Object>> endTrip(@PathVariable Long requestId,
                                                       @PathVariable Long driverId,
                                                       @PathVariable Long customerId) {
        try {
            // Fetch RideRequest by requestId
            Optional<RideRequest> optionalRideRequest = rideRequestRepository.findById(requestId);

            if (optionalRideRequest.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "RideRequest not found for the given requestId"));
            }

            RideRequest rideRequest = optionalRideRequest.get();

            // Validate that the rideRequest matches the provided driverId and customerId
            if (!rideRequest.getDriver().getId().equals(driverId) || !rideRequest.getCustomer().getId().equals(customerId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Driver or Customer does not match the RideRequest"));
            }

            // Ensure DriverDetails is associated with the RideRequest
            DriverDetails driverDetails = rideRequest.getDriverDetails();
            if (driverDetails == null || !driverDetails.getDriverId().equals(driverId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Driver details not associated with the ride request"));
            }

            // Update DriverDetails status to "complete"
            if ("incomplete".equals(driverDetails.getStatus())) {
                driverDetails.setStatus("complete");
                driverDetailsRepository.save(driverDetails);
            }

            // Calculate the distance using pickup and drop-off coordinates
            double distance = calculateDistance(
                    rideRequest.getPickupLatitude(),
                    rideRequest.getPickupLongitude(),
                    rideRequest.getDropOffLatitude(),
                    rideRequest.getDropOffLongitude()
            );

            // Calculate the total price using the rate per km from the CarService
            CarService carService = rideRequest.getCarService();
            if (carService == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Car service not associated with the ride request"));
            }

            double ratePerKm = carService.getRatePerKm(); // Retrieve rate from CarService
            double totalAmount = distance * ratePerKm; // Calculate total price based on distance

            // Update RideRequest status to "COMPLETED"
            rideRequest.setStatus("completed");
            rideRequest.setTotalPrice(totalAmount);
            rideRequestRepository.save(rideRequest);

            // Save ride history
            RideHistory rideHistory = new RideHistory();
            rideHistory.setCustomer(rideRequest.getCustomer());
            rideHistory.setPickupLatitude(rideRequest.getPickupLatitude());
            rideHistory.setPickupLongitude(rideRequest.getPickupLongitude());
            rideHistory.setDropOffLatitude(rideRequest.getDropOffLatitude());
            rideHistory.setDropOffLongitude(rideRequest.getDropOffLongitude());
            rideHistory.setDistance(distance);
            rideHistory.setTotalAmount(Math.round(totalAmount));
            rideHistory.setServiceName(carService.getName());
            rideHistory.setPrice(Math.round(totalAmount));
            rideHistory.setVehicleType(rideRequest.getVehicleType());
            rideHistory.setDateCompleted(new Date());

            rideHistoryRepository.save(rideHistory);

            // Return response with status
            Map<String, Object> response = Map.of(
                    "message", "Trip ended successfully",
                    "rideRequestId", rideRequest.getId(),
                    "distance", distance,
                    "totalAmount", totalAmount,
                    "price", totalAmount,
                    "serviceName", carService.getName(),
                    "vehicleType", rideRequest.getVehicleType()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred"));
        }
    }

    // Helper method to calculate distance between two geo-coordinates
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Returns the distance in km
    }
}
