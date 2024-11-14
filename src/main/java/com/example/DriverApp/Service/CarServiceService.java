package com.example.DriverApp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DriverApp.Entities.CarService;
import com.example.DriverApp.Entities.Customer;
import com.example.DriverApp.Repositories.CarServiceRepository;
import com.example.DriverApp.Repositories.CustomerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceService {

    @Autowired
    private CarServiceRepository carServiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Create or Update a Car Service
    public CarService saveCarService(CarService carService) {
        return carServiceRepository.save(carService);
    }

    // Create or Update multiple Car Services
    public List<CarService> saveAllCarServices(List<CarService> carServices) {
        return carServiceRepository.saveAll(carServices); // Save all car services individually
    }

    // Read/Get a Car Service by serviceName
    public List<CarService> getCarServicesByServiceName(String serviceName) {
        return carServiceRepository.findAllByServiceName(serviceName); // Return list of car services by service name
    }

    // Update Car Service by serviceName
    public CarService updateCarService(String serviceName, CarService updatedService) {
        List<CarService> existingServices = carServiceRepository.findAllByServiceName(serviceName);
        if (!existingServices.isEmpty()) {
            CarService carService = existingServices.get(0); // Assuming we update the first service found
            carService.setDescription(updatedService.getDescription());
            carService.setVehicleType(updatedService.getVehicleType()); // Update car type
            carService.setRatePerKm(updatedService.getRatePerKm()); // Update rate
            return carServiceRepository.save(carService);
        }
        return null;
    }

    // Get all Car Services
    public List<CarService> getAllCarServices() {
        return carServiceRepository.findAll();
    }

    // Delete a Car Service by ID
    public void deleteCarService(Long id) {
        carServiceRepository.deleteById(id);
    }

    // Method to get all vehicle types with estimated prices for a given service name and drop-off location
    public List<CarServiceResponse> getAllVehicleTypesWithPrices(String serviceName, Long customerId, double dropOffLatitude, double dropOffLongitude) {
        // Fetch all car services for the selected service name
        List<CarService> carServices = carServiceRepository.findAllByServiceName(serviceName);  // Fetch a list of car services

        // Fetch the customer's pickup location
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        double pickupLatitude = customer.getLatitude();
        double pickupLongitude = customer.getLongitude();

        // Calculate price for each vehicle type based on the distance between pickup and drop-off locations
        return carServices.stream()
                .map(carService -> {
                    double distance = calculateDistance(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);
                    double price = distance * carService.getRatePerKm();  // Price = distance * rate per km
                    return new CarServiceResponse(carService.getVehicleType(), price);
                })
                .collect(Collectors.toList());
    }

    // Haversine formula to calculate distance in kilometers
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Radius of Earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
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
}
