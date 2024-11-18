package com.example.DriverApp.Controller;

import com.example.DriverApp.DTO.ApiResponse;
import com.example.DriverApp.DTO.LoginRequest;
import com.example.DriverApp.DTO.LoginResponse;
import com.example.DriverApp.Entities.Driver;
import com.example.DriverApp.Entities.RideRequest;
import com.example.DriverApp.Service.DriverService;
import com.example.DriverApp.Utility.Mapper;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/open/drivers")
@CrossOrigin(origins = "*")
public class DriverController {

    @Autowired
    private DriverService driverService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Driver>> registerDriver(
            @RequestParam("driver") String driverJson,
            @RequestParam("criminalBackgroundCheckFile") MultipartFile criminalBackgroundCheckFile,
            @RequestParam("licenseNumberFile") MultipartFile licenseNumberFile,
            @RequestParam("insuranceDetailsFile") MultipartFile insuranceDetailsFile) {

        try {
            // Deserialize JSON string to Driver object
            Driver driver = Mapper.stringToClass(driverJson, Driver.class);

            // Handle null deserialization
            if (driver == null) {
                throw new Exception("Invalid driver JSON. Deserialization resulted in null.");
            }

            // Save driver
            Driver savedDriver = driverService.saveDriver(driver, criminalBackgroundCheckFile, licenseNumberFile, insuranceDetailsFile);

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, savedDriver, "Driver registered successfully. Awaiting approval."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, null, e.getMessage()));
        }
    }

    @PostMapping("/approve/{driverId}")
    public ResponseEntity<ApiResponse<Driver>> approveDriver(@PathVariable Long driverId) {
        try {
            Driver approvedDriver = driverService.approveDriver(driverId);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, approvedDriver, "Driver approved successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, null, e.getMessage()));
        }
    }

    @PostMapping("/reject/{driverId}")
    public ResponseEntity<ApiResponse<Driver>> rejectDriver(@PathVariable Long driverId) {
        try {
            Driver rejectedDriver = driverService.rejectDriver(driverId);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, rejectedDriver, "Driver rejected successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, null, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse loginResponse = driverService.login(loginRequest);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, loginResponse, "Login successful."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, null, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Driver>> getDriverById(@PathVariable Long id) {
        try {
            Driver driver = driverService.getDriverById(id);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, driver, "Driver retrieved successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(HttpStatus.NOT_FOUND, null, e.getMessage()));
        }
    }

    @PostMapping("/{driverId}/uploadProfilePicture")
    public ResponseEntity<ApiResponse<String>> uploadProfilePicture(
            @PathVariable Long driverId, 
            @RequestParam("file") MultipartFile file) {
        try {
            Driver driver = driverService.uploadProfilePicture(driverId, file);
            String profilePictureUrl = driver.getProfilePictureUrl();
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, profilePictureUrl, "Profile picture uploaded successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, null, e.getMessage()));
        }
    }

    @GetMapping("/{driverId}/profilePicture")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable Long driverId) {
        try {
            Resource resource = driverService.getProfilePicture(driverId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType("image/jpeg"))
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/generate-reset-password/otp")
    public ResponseEntity<ApiResponse<String>> generateOtp(@RequestParam String email) {
        try {
            String response = driverService.generateOtp(email);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, response, "OTP sent successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, null, e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestParam String otp, @RequestParam String newPassword) {
        try {
            String response = driverService.resetPassword(otp, newPassword);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, response, "Password reset successfully."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, null, e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestParam Long driverId) {
        try {
            driverService.logout(driverId);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Logged out successfully.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, null, e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Driver>>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, drivers, "All drivers retrieved successfully."));
    }

    @PutMapping("/{id}/status-disable/enable")
    public ResponseEntity<ApiResponse<String>> toggleDriverStatus(@PathVariable Long id, @RequestParam boolean enable) {
        boolean isUpdated = driverService.toggleDriverStatus(id, enable);
        if (isUpdated) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Driver status updated successfully.", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Driver not found."));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDriver(@PathVariable Long id) {
        ApiResponse<String> response = driverService.deleteDriverById(id);

        if (response.getStatus() == HttpStatus.OK) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
