package com.example.DriverApp.Controller;

import com.example.DriverApp.DTO.AdminLoginRequest;
import com.example.DriverApp.DTO.AdminResponse;
import com.example.DriverApp.DTO.ApiResponse;
import com.example.DriverApp.DTO.ChangePasswordRequest;
import com.example.DriverApp.DTO.LoginRequest;
import com.example.DriverApp.DTO.UploadResponse;
import com.example.DriverApp.Entities.Admin;
import com.example.DriverApp.Service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.io.IOException;
import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api/open/admins")
@CrossOrigin(origins = "*")

public class AdminController {

    @Autowired
    private AdminService adminService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Register (Create) new admin
    @PostMapping("/register")
    public ResponseEntity<Admin> registerAdmin(@RequestBody Admin admin) {
        Admin registeredAdmin = adminService.registerAdmin(admin);
        return ResponseEntity.status(200).body(registeredAdmin); 
    }

    // Get all admins
    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.status(200).body(admins); 
    }

    // Get admin by ID
    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        Optional<Admin> admin = adminService.getAdminById(id);
        if (admin.isPresent()) {
            return ResponseEntity.status(200).body(admin.get()); 
        } else {
            return ResponseEntity.status(404).body(null); 
        }
    }

    // Update admin
    @PutMapping("/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @RequestBody Admin updatedAdmin) {
        Admin admin = adminService.updateAdmin(id, updatedAdmin);
        if (admin != null) {
            return ResponseEntity.status(200).body(admin); 
        } else {
            return ResponseEntity.status(404).body(null); 
        }
    }

    // Delete admin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.status(200).build(); 
    }

    // Admin login
    @PostMapping("/login")
    public ResponseEntity<AdminResponse> login(@RequestBody AdminLoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
    
        AdminResponse response = adminService.login(email, password);
        return ResponseEntity.ok(response);
    }
    
    

    // Change password
 
@PutMapping("/change-password")
public ResponseEntity<ApiResponse<String>> changePassword(
        @RequestBody ChangePasswordRequest request) {

    try {
        adminService.changePassword(request.getEmail(), request.getOldPassword(), request.getNewPassword());
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.OK, "Password changed successfully.", null);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (RuntimeException e) {
        ApiResponse<String> response = new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

 // Upload admin picture
 @PostMapping("/{adminId}/upload-picture")
 public ResponseEntity<UploadResponse> uploadAdminPicture(@PathVariable Long adminId, @RequestParam("file") MultipartFile file) {
     try {
         String message = adminService.uploadAdminPicture(adminId, file);
         String pictureUrl = "URL_OF_THE_PICTURE"; 
         UploadResponse response = new UploadResponse(message, pictureUrl);
         return ResponseEntity.ok(response);
     } catch (IOException e) {
         return ResponseEntity.status(500).body(new UploadResponse("Failed to upload picture: " + e.getMessage(), null));
     } catch (RuntimeException e) {
         return ResponseEntity.status(404).body(new UploadResponse(e.getMessage(), null));
     }
 }

 // Get admin picture
 @GetMapping("/{adminId}/picture")
 public ResponseEntity<byte[]> getAdminPicture(@PathVariable Long adminId) {
     try {
         return adminService.getAdminPicture(adminId);
     } catch (IOException e) {
         return ResponseEntity.status(500).body(null);
     } catch (RuntimeException e) {
         return ResponseEntity.status(404).body(null);
     }
 }
}
