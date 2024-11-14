package com.example.DriverApp.Service;

import com.example.DriverApp.DTO.AdminResponse;
import com.example.DriverApp.Entities.Admin;
import com.example.DriverApp.Repositories.AdminRepository;
import com.example.DriverApp.Utility.JwtUtil; // Import the JwtUtil class
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil; 

    @Autowired
    private CloudinaryService cloudinaryService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

     public Admin registerAdmin(Admin admin) {
         admin.setPassword(passwordEncoder.encode(admin.getPassword()));

         admin.setEmployeeNumber(generateEmployeeNumber());

        return adminRepository.save(admin);
    }

     private String generateEmployeeNumber() {
        long count = adminRepository.count();  // Get the current number of admins in the database
        return "emp/" + String.format("%04d", count + 1);  // Generate a number like emp/0001, emp/0002, etc.
    }

   public AdminResponse login(String email, String password) {
    Admin admin = adminRepository.findByEmail(email);

    if (admin != null && passwordEncoder.matches(password, admin.getPassword())) {
        String token = jwtUtil.generateTokenWithExpiration(email, 1); // 1-day expiration

        // Return an instance of AdminResponse instead of Map
        return new AdminResponse(admin.getId(), token);
    }

     throw new RuntimeException("Invalid credentials"); // Throw a meaningful exception
     
}
    // Get all admins
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // Get admin by ID
    public Optional<Admin> getAdminById(Long id) {
        return adminRepository.findById(id);
    }

    // Get admin by email
    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    // Update Admin
    public Admin updateAdmin(Long id, Admin updatedAdmin) {
        Optional<Admin> existingAdmin = adminRepository.findById(id);
        if (existingAdmin.isPresent()) {
            Admin admin = existingAdmin.get();
            admin.setName(updatedAdmin.getName());
            admin.setPhoneNumber(updatedAdmin.getPhoneNumber());
            admin.setEmployeeNumber(updatedAdmin.getEmployeeNumber());
            admin.setEmail(updatedAdmin.getEmail());

            // Only update password if it's not null or empty
            if (updatedAdmin.getPassword() != null && !updatedAdmin.getPassword().isEmpty()) {
                admin.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));
            }

            return adminRepository.save(admin);
        }
        throw new RuntimeException("Admin not found"); // Throw an exception for not found admins
    }

    // Change password method
    public void changePassword(String email, String oldPassword, String newPassword) {
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            // Check if the old password matches the stored hashed password
            if (passwordEncoder.matches(oldPassword, admin.getPassword())) {
                // Ensure the new password is not empty or the same as the old password
                if (newPassword != null && !newPassword.isEmpty() && !passwordEncoder.matches(newPassword, admin.getPassword())) {
                    // Hash and set the new password
                    admin.setPassword(passwordEncoder.encode(newPassword));
                    adminRepository.save(admin); // Save the updated password
                } else {
                    throw new RuntimeException("New password cannot be empty or the same as the old password.");
                }
            } else {
                throw new RuntimeException("Old password is incorrect.");
            }
        } else {
            throw new RuntimeException("Admin not found.");
        }
    }

    // Delete Admin
    public void deleteAdmin(Long id) {
        if (adminRepository.existsById(id)) {
            adminRepository.deleteById(id);
        } else {
            throw new RuntimeException("Admin not found"); // Handle case where admin doesn't exist
        }
    }


    public String uploadAdminPicture(Long adminId, MultipartFile file) throws IOException {
        Optional<Admin> adminOptional = adminRepository.findById(adminId);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();

            // Upload the file to Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file);
            
            // Extract the URL from the upload result
            String pictureUrl = (String) uploadResult.get("url");

            // Set the picture URL in the admin entity
            admin.setPicturePath(pictureUrl);

            // Save the updated admin
            adminRepository.save(admin);

            // Return success message or the picture URL
            return "Picture uploaded successfully: " + pictureUrl;
        } else {
            throw new RuntimeException("Admin not found");
        }
    }

    // Get admin picture by ID
    public ResponseEntity<byte[]> getAdminPicture(Long adminId) throws IOException {
        Optional<Admin> adminOptional = adminRepository.findById(adminId);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            String picturePath = admin.getPicturePath();
            
            // Ensure picture path is not null or empty
            if (picturePath != null && !picturePath.isEmpty()) {
                // Fetch the image directly from the Cloudinary URL
                // Convert the URL to an InputStream
                byte[] imageBytes = Files.readAllBytes(Paths.get(picturePath)); // Change this if necessary to handle Cloudinary URLs
                
                // Return the image bytes in the response entity
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + picturePath + "\"")
                        .contentType(MediaType.IMAGE_JPEG) // Assuming JPEG, adjust for different formats
                        .body(imageBytes);
            } else {
                throw new RuntimeException("No picture found for this admin.");
            }
        } else {
            throw new RuntimeException("Admin not found");
        }
    }
}