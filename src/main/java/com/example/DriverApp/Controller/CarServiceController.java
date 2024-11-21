package com.example.DriverApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.DriverApp.DTO.UpdateResponse;
import com.example.DriverApp.Entities.CarService;
import com.example.DriverApp.Service.CarServiceService;

import java.util.List;

@RestController
@RequestMapping("/api/open/services")
@CrossOrigin(origins = "*")
public class CarServiceController {

    @Autowired
    private CarServiceService carServiceService;

    // Endpoint to add a single Car Service
    @PostMapping("/add")
    public ResponseEntity<CarService> addCarService(@RequestBody CarService carService) {
        if (carService == null) {
            return ResponseEntity.badRequest().build();
        }
        CarService savedCarService = carServiceService.saveCarService(carService);
        return ResponseEntity.ok(savedCarService);
    }

    // Endpoint to add multiple Car Services
    @PostMapping("/addAll")
    public ResponseEntity<List<CarService>> addCarServices(@RequestBody List<CarService> carServices) {
        if (carServices == null || carServices.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<CarService> savedCarServices = carServiceService.saveAllCarServices(carServices);
        return ResponseEntity.ok(savedCarServices);
    }

    // Endpoint to update Car Services by serviceName
   @PutMapping("/update/{serviceName}")
public ResponseEntity<UpdateResponse> updateCarServices(
        @PathVariable String serviceName,
        @RequestBody List<CarService> updatedServices) {
    UpdateResponse response = carServiceService.updateCarServices(serviceName, updatedServices);
    
    if (response.getUpdatedServices() != null && !response.getUpdatedServices().isEmpty()) {
        return ResponseEntity.ok(response);  // Return updated services and success message
    }
    
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);  // Return not found with message
}

    
    
    // Endpoint to get Car Services by serviceName
    @GetMapping("/get/{serviceName}")
    public ResponseEntity<List<CarService>> getCarServicesByServiceName(@PathVariable String serviceName) {
        List<CarService> carServices = carServiceService.getCarServicesByServiceName(serviceName);
        if (carServices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carServices);
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

    // Endpoint to get all vehicle types with estimated prices
    @GetMapping("/prices")
    public ResponseEntity<List<CarServiceService.CarServiceResponse>> getAllVehicleTypesWithPrices(
            @RequestParam String serviceName,
            @RequestParam Long customerId,
            @RequestParam double dropOffLatitude,
            @RequestParam double dropOffLongitude) {

        List<CarServiceService.CarServiceResponse> responses = carServiceService.getAllVehicleTypesWithPrices(
                serviceName, customerId, dropOffLatitude, dropOffLongitude);
        return ResponseEntity.ok(responses);
    }
}
