package com.example.DriverApp.Controller;

import com.example.DriverApp.Entities.ServiceEntity;
import com.example.DriverApp.Service.ServiceService;
import com.example.DriverApp.Utility.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/open/services")
@CrossOrigin(origins = "*")
public class ServiceController {

    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);


    @Autowired
    private ServiceService serviceService;

    // Create new service
    @PostMapping
    public ResponseEntity<ServiceEntity> createService(@RequestPart("service") String service, 
                                                       @RequestPart("file") MultipartFile file) {
        try {
            ServiceEntity createdService = serviceService.createService(service, file);
            return new ResponseEntity<>(createdService, HttpStatus.CREATED);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Retrieve all services
    @GetMapping
    public ResponseEntity<List<ServiceEntity>> getAllServices() {
        List<ServiceEntity> services = serviceService.getAllServices();
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    // Retrieve a service by ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntity> getServiceById(@PathVariable Long id) {
        Optional<ServiceEntity> service = serviceService.getServiceById(id);
        return service.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // update a sevice 
      @PutMapping("/{id}")
      public ResponseEntity<ServiceEntity> updateService(@PathVariable Long id,
                                                         @RequestPart("service") String serviceDetailsJson,
                                                         @RequestPart(value = "file", required = false) MultipartFile file) {
          log.info("Received request to update service with ID: {}", id);
  
          try {
              ServiceEntity serviceDetails = Mapper.stringToClass(serviceDetailsJson, ServiceEntity.class);
              log.debug("Deserialized service details: {}", serviceDetails);
  
              if (serviceDetails == null) {
                  log.warn("Failed to deserialize service details for ID: {}", id);
                  return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
              }
  
              if (file != null) {
                  log.info("Received file for upload with name: {}", file.getOriginalFilename());
              } else {
                  log.info("No file provided for update request with ID: {}", id);
              }
  
              // Update the service
              ServiceEntity updatedService = serviceService.updateService(id, serviceDetails, file);
              if (updatedService != null) {
                  log.info("Successfully updated service with ID: {}", id);
                  return ResponseEntity.ok(updatedService);
              } else {
                  log.warn("Service with ID: {} not found, update operation unsuccessful", id);
                  return ResponseEntity.notFound().build();
              }
          } catch (IOException e) {
              log.error("Error updating service with ID: {}: {}", id, e.getLocalizedMessage());
              return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
          }
      }
  



      

    // Delete a service
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        return serviceService.deleteService(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
