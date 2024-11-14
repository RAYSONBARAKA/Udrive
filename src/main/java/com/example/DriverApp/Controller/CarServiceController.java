package com.example.DriverApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.DriverApp.Entities.CarService;
import com.example.DriverApp.Service.CarServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/open/services")
public class CarServiceController {

    @Autowired
    private CarServiceService carServiceService;

    // Endpoint to get a Car Service by serviceName for editing
    @GetMapping("/edit/{serviceName}")
    public ResponseEntity<CarService> editCarService(@PathVariable String serviceName) {
        List<CarService> carServices = carServiceService.getCarServicesByServiceName(serviceName);
        if (carServices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carServices.get(0)); // Return the first service found for simplicity
    }

    // Endpoint to update Car Service details by serviceName
    @PutMapping("/update/{serviceName}")
    public ResponseEntity<CarService> updateCarService(@PathVariable String serviceName, @RequestBody CarService updatedService) {
        CarService updatedCarService = carServiceService.updateCarService(serviceName, updatedService);
        if (updatedCarService != null) {
            return ResponseEntity.ok(updatedCarService);
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint to add multiple Car Services
    @PostMapping("/add")
    public ResponseEntity<List<CarService>> addCarServices(@RequestBody List<CarService> carServices) {
        if (carServices == null || carServices.isEmpty()) {
            return ResponseEntity.badRequest().build(); // Return bad request if no services are provided
        }

        List<CarService> savedCarServices = carServiceService.saveAllCarServices(carServices);
        return ResponseEntity.ok(savedCarServices); // Return the list of saved car services
    }

    // Endpoint to get all vehicle types with estimated prices for a given service name and drop-off location
    @GetMapping("/prices")
    public ResponseEntity<List<CarServiceService.CarServiceResponse>> getAllVehicleTypesWithPrices(
            @RequestParam String serviceName,
            @RequestParam Long customerId,
            @RequestParam double dropOffLatitude,
            @RequestParam double dropOffLongitude) {

        // Get all car services with prices for the given service name, customer ID, and drop-off location
        List<CarServiceService.CarServiceResponse> carServiceResponses = carServiceService.getAllVehicleTypesWithPrices(
                serviceName, customerId, dropOffLatitude, dropOffLongitude);

        return ResponseEntity.ok(carServiceResponses); // Return the list of vehicle types and their prices
    }
}
