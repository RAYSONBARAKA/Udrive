package com.example.DriverApp.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DriverApp.Entities.CarService;
import com.example.DriverApp.Entities.Customer;
import com.example.DriverApp.Repositories.CarServiceRepository;
import com.example.DriverApp.Repositories.CustomerRepository;
import com.example.DriverApp.DTO.CarServiceResponse;

@Service
public class CarServiceService {

    @Autowired
    private CarServiceRepository carServiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Create or Update a single Car Service
    public CarService saveCarService(CarService carService) {
        return carServiceRepository.save(carService);
    }

    // Get a single Car Service by serviceName
    public CarService getCarServiceByServiceName(String serviceName) {
        return carServiceRepository.findByServiceName(serviceName)
                .orElseThrow(() -> new RuntimeException("Car Service not found"));
    }

    // Update a single Car Service by serviceName
    public CarService updateCarService(String serviceName, CarService updatedService) {
        CarService existingService = carServiceRepository.findByServiceName(serviceName)
                .orElseThrow(() -> new RuntimeException("Car Service not found"));

        existingService.setDescription(updatedService.getDescription());
        existingService.setVehicleType(updatedService.getVehicleType());
        existingService.setRatePerKm(updatedService.getRatePerKm());
        existingService.setName(updatedService.getName());
        existingService.setDistance(updatedService.getDistance());

        return carServiceRepository.save(existingService);
    }

    // Get all Car Services
    public List<CarService> getAllCarServices() {
        return carServiceRepository.findAll();
    }

    // Delete a Car Service by ID
    public void deleteCarService(Long id) {
        carServiceRepository.deleteById(id);
    }

    // Get vehicle type with price for a specific service
    public CarServiceResponse getVehicleTypeWithPrice(String serviceName,
                                                      Long customerId,
                                                      double dropOffLatitude,
                                                      double dropOffLongitude) {
        // Find car service by service name only
        CarService carService = carServiceRepository.findByServiceName(serviceName)
                .orElseThrow(() -> new RuntimeException("Car Service not found"));

        // Find customer by ID
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Get pickup coordinates from customer details
        double pickupLatitude = customer.getLatitude();
        double pickupLongitude = customer.getLongitude();

        // Calculate the distance between pickup and drop-off points
        double distance = calculateDistance(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);

        // Calculate the price based on the rate per km
        double price = distance * carService.getRatePerKm();

        // Return response
        return new CarServiceResponse(carService.getVehicleType(), carService.getRatePerKm(), price);
    }

    // Haversine formula to calculate distance in kilometers
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Radius of the Earth in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // Return the distance in kilometers
    }
}
