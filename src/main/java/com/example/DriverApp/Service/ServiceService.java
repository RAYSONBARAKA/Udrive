package com.example.DriverApp.Service;

import com.example.DriverApp.Entities.ServiceEntity;
import com.example.DriverApp.Repositories.ServiceRepository;
import com.example.DriverApp.Utility.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CloudinaryService cloudinaryService; 

    // Create new service with photo upload
    public ServiceEntity createService(String service, MultipartFile file) throws IOException {
        ServiceEntity serviceEntity = new ServiceEntity();
        if (file != null && !file.isEmpty()) {
            // the file to Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file);
            
            // Extract the URL from the upload result
            String photoUrl = (String) uploadResult.get("url"); 
            serviceEntity = Mapper.stringToClass(service, ServiceEntity.class);
            // Set the photo URL in the service entity
            serviceEntity.setPhotoPath(photoUrl);
        }

        // Set price and distance 
        serviceEntity.setPrice(serviceEntity.getPrice());
        serviceEntity.setDistance(serviceEntity.getDistance());

        // Save the service entity to the database
        return serviceRepository.save(serviceEntity);
    }

    // Retrieve all services
    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    // Retrieve a service by ID
    public Optional<ServiceEntity> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }





    
    public ServiceEntity updateService(Long id, ServiceEntity serviceDetails, MultipartFile file) throws IOException {
        // Retrieve the service by ID or throw an exception if not found
        ServiceEntity existingService = serviceRepository.findById(id)
                .orElseThrow();
    
        // Update fields in the existing service
        existingService.setServiceName(serviceDetails.getServiceName());
        existingService.setDescription(serviceDetails.getDescription());
        existingService.setPrice(serviceDetails.getPrice());
        existingService.setDistance(serviceDetails.getDistance());
    
        // Check if a new file is provided for updating the photo
        if (file != null && !file.isEmpty()) {
            // Upload the new file to Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file);
            
            // Extract the URL from the upload result
            String photoUrl = (String) uploadResult.get("url"); // or use "secure_url" for HTTPS
    
            // Update the photo URL in the service entity
            existingService.setPhotoPath(photoUrl);
        }
    
        // Save and return the updated service entity
        return serviceRepository.save(existingService);
    }
    

    // Delete a service
    public boolean deleteService(Long id) {
        Optional<ServiceEntity> optionalService = serviceRepository.findById(id);

        if (optionalService.isPresent()) {
            serviceRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }


}
