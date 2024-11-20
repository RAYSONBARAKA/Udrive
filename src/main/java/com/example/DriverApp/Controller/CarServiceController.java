package com.example.DriverApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.DriverApp.Entities.CarService;
import com.example.DriverApp.Service.CarServiceService;
import com.example.DriverApp.DTO.CarServiceResponse;

import java.util.List;

@RestController
@RequestMapping("/api/open/services")
@CrossOrigin(origins = "*")
public class CarServiceController {

    @Autowired
    private CarServiceService carServiceService;

    // Endpoint to add or update a single Car Service
    @PostMapping("/add")
    public ResponseEntity<CarService> addCarService(@RequestBody CarService carService) {
        if (carService == null) {
            return ResponseEntity.badRequest().build();
        }
        CarService savedCarService = carServiceService.saveCarService(carService);
        return ResponseEntity.ok(savedCarService);
    }

    // Endpoint to update a single Car Service by serviceName
    @PutMapping("/update/{serviceName}")
    public ResponseEntity<CarService> updateCarService(
            @PathVariable String serviceName,
            @RequestBody CarService updatedService) {
        CarService updatedCarService = carServiceService.updateCarService(serviceName, updatedService);
        return ResponseEntity.ok(updatedCarService);
    }

    // Endpoint to get a single Car Service by serviceName
    @GetMapping("/get/{serviceName}")
    public ResponseEntity<CarService> getCarServiceByServiceName(
            @PathVariable String serviceName) {
        CarService carService = carServiceService.getCarServiceByServiceName(serviceName);
        return ResponseEntity.ok(carService);
    }

    // Endpoint to get all Car Services
    @GetMapping("/all")
    public ResponseEntity<List<CarService>> getAllCarServices() {
        List<CarService> carServices = carServiceService.getAllCarServices();
        return ResponseEntity.ok(carServices);
    }

    // Endpoint to delete a Car Service by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCarService(@PathVariable Long id) {
        carServiceService.deleteCarService(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint to get vehicle type with price for a specific service
    @GetMapping("/price")
    public ResponseEntity<CarServiceResponse> getVehicleTypeWithPrice(
            @RequestParam String serviceName,
            @RequestParam Long customerId,
            @RequestParam double dropOffLatitude,
            @RequestParam double dropOffLongitude) {
        CarServiceResponse response = carServiceService.getVehicleTypeWithPrice(
                serviceName, customerId, dropOffLatitude, dropOffLongitude);
        return ResponseEntity.ok(response);
    }
}
