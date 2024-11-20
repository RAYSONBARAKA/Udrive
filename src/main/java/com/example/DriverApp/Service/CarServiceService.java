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
        return carServiceRepository.saveAll(carServices);
    }

    // Read/Get Car Services by serviceName
    public List<CarService> getCarServicesByServiceName(String serviceName) {
        return carServiceRepository.findAllByServiceName(serviceName);
    }

    // Update Car Services by serviceName
    public List<CarService> updateCarServices(String serviceName, List<CarService> updatedServices) {
        List<CarService> existingServices = carServiceRepository.findAllByServiceName(serviceName);
        if (!existingServices.isEmpty()) {
            for (int i = 0; i < existingServices.size() && i < updatedServices.size(); i++) {
                CarService existingService = existingServices.get(i);
                CarService updatedService = updatedServices.get(i);

                existingService.setDescription(updatedService.getDescription());
                existingService.setVehicleType(updatedService.getVehicleType());
                existingService.setRatePerKm(updatedService.getRatePerKm());
                carServiceRepository.save(existingService);
            }
            return carServiceRepository.findAllByServiceName(serviceName);
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

    // Get all vehicle types with estimated prices
    public List<CarServiceResponse> getAllVehicleTypesWithPrices(
            String serviceName, Long customerId, double dropOffLatitude, double dropOffLongitude) {
        List<CarService> carServices = carServiceRepository.findAllByServiceName(serviceName);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        double pickupLatitude = customer.getLatitude();
        double pickupLongitude = customer.getLongitude();

        return carServices.stream()
                .map(carService -> {
                    double distance = calculateDistance(pickupLatitude, pickupLongitude, dropOffLatitude, dropOffLongitude);
                    double price = distance * carService.getRatePerKm(); // Use car-specific rate per km
                    return new CarServiceResponse(carService.getVehicleType(), carService.getRatePerKm(), price);
                })
                .collect(Collectors.toList());
    }

    // Haversine formula to calculate distance in kilometers
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    // DTO class for car service response
    public static class CarServiceResponse {
        private String vehicleType;
        private double ratePerKm;
        private double estimatedPrice;

        public CarServiceResponse(String vehicleType, double ratePerKm, double estimatedPrice) {
            this.vehicleType = vehicleType;
            this.ratePerKm = ratePerKm;
            this.estimatedPrice = estimatedPrice;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public double getRatePerKm() {
            return ratePerKm;
        }

        public double getEstimatedPrice() {
            return estimatedPrice;
        }
    }
}
