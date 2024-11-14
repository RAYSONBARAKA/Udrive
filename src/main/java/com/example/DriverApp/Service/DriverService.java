package com.example.DriverApp.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.management.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.example.DriverApp.DTO.ApiResponse;
import com.example.DriverApp.DTO.LoginRequest;
import com.example.DriverApp.DTO.LoginResponse;
import com.example.DriverApp.Entities.Driver;
import com.example.DriverApp.Entities.RideRequest;
import com.example.DriverApp.Repositories.DriverRepository;
import com.example.DriverApp.Repositories.NotificationRepository;
import com.example.DriverApp.Repositories.RideRequestRepository;
import com.example.DriverApp.Utility.JwtUtil;
import com.example.DriverApp.WebSocketHandler.RideRequestWebSocketHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class DriverService {

    private static final Logger logger = LoggerFactory.getLogger(DriverService.class);


    @Autowired
    private DriverRepository driverRepository;

    @Autowired
private NotificationRepository notificationRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final String uploadDirectory = "C:\\Users\\RAYSON\\Pictures\\Saved Pictures\\";

    // OTP storage
    private String generatedOtp;
    private String otpEmail;

    // ==================== Driver Management ====================

    public Driver saveDriver(
        Driver driver, 
        MultipartFile criminalBackgroundCheckFile, 
        MultipartFile licenseNumberFile, 
        MultipartFile insuranceDetailsFile) throws Exception {
    
        // Check if the email already exists
        if (emailExists(driver.getEmail())) {
            throw new Exception("Email is already registered.");
        }
    
        // Encrypt the password
        driver.setPassword(passwordEncoder.encode(driver.getPassword()));
        driver.setStatus("Pending");  // Set status to "Pending" initially
    
        // Upload files to Cloudinary and get URLs
        String criminalBackgroundCheckUrl = uploadFileAndGetUrl(criminalBackgroundCheckFile, "Criminal Background Check");
        String licenseNumberUrl = uploadFileAndGetUrl(licenseNumberFile, "License Number");
        String insuranceDetailsUrl = uploadFileAndGetUrl(insuranceDetailsFile, "Insurance Details");
    
        // Set the URLs in the driver entity
        driver.setCriminalBackgroundCheckUrl(criminalBackgroundCheckUrl);
        driver.setLicenseNumberUrl(licenseNumberUrl);
        driver.setInsuranceDetailsUrl(insuranceDetailsUrl);
    
        // Save the driver with all details and documents
        return driverRepository.save(driver);
    }
    private String uploadFileAndGetUrl(MultipartFile file, String fileType) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new Exception(fileType + " file is required and cannot be empty.");
        }
    
        try {
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file);
            String fileUrl = (String) uploadResult.get("url");
    
            if (fileUrl == null || fileUrl.isEmpty()) {
                throw new Exception(fileType + " upload failed: URL is empty.");
            }
    
            // Log the upload result (optional, for debugging)
            logger.info(fileType + " uploaded successfully: " + fileUrl);
    
            return fileUrl;
        } catch (IOException e) {
            throw new Exception(fileType + " upload failed: " + e.getMessage());
        }
    }
    
    // Approve the driver by setting status to "Approved"
    public Driver approveDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        driver.setStatus("Approved");  // Change status to "Approved"
        driverRepository.save(driver);

        emailService.sendApprovalEmail(driver.getEmail(), driver.getFullName());
        return driver;
    }

    // Reject the driver by setting status to "Rejected"
    public Driver rejectDriver(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        driver.setStatus("Rejected");  // Change status to "Rejected"
        driverRepository.save(driver);

        emailService.sendRejectionEmail(driver.getEmail(), driver.getFullName());
        return driver;
    }

    // ==================== Authentication ====================

    // Login method
    public LoginResponse login(LoginRequest loginRequest) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findByEmail(loginRequest.getEmail());

        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), driver.getPassword())) {
                // Update availability and location when logged in
                driver.setAvailable(true);
                driver.setLatitude(loginRequest.getLatitude());
                driver.setLongitude(loginRequest.getLongitude());
                driverRepository.save(driver);  // Save the changes to the database

                String token = jwtUtil.generateTokenWithExpiration(driver.getEmail(), 1);  // Token valid for 1 day
                return new LoginResponse(driver.getId(), token);
            } else {
                throw new Exception("Invalid password.");
            }
        } else {
            throw new Exception("Driver not found.");
        }
    }

    // Logout method
    public void logout(Long driverId) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findById(driverId);

        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setAvailable(false);  // Update availability to false when logged out
            driverRepository.save(driver);  // Save the availability change to the database
        } else {
            throw new Exception("Driver not found.");
        }
    }

    // ==================== Driver Retrieval ====================

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();  
    }

    // ==================== Profile Management ====================

    // Upload profile picture for driver
    public Driver uploadProfilePicture(Long driverId, MultipartFile file) throws Exception {
        Driver driver = getDriverById(driverId);

        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new Exception("Invalid file. Please select a valid file to upload.");
        }

        // Upload the file to Cloudinary and get the URL
        Map<String, Object> uploadResult = cloudinaryService.uploadFile(file);
        String cloudinaryUrl = (String) uploadResult.get("url");

        // Update the driver's profile picture URL in the database
        driver.setProfilePictureUrl(cloudinaryUrl);
        return driverRepository.save(driver);
    }

    // Retrieve the profile picture URL for download/display
    public Resource getProfilePicture(Long driverId) throws Exception {
        Driver driver = getDriverById(driverId);
        String profilePictureUrl = driver.getProfilePictureUrl();

        if (profilePictureUrl == null || profilePictureUrl.isEmpty()) {
            throw new RuntimeException("No profile picture found for driver with id: " + driverId);
        }

        try {
            return new UrlResource(profilePictureUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + profilePictureUrl, e);
        }
    }

    // ==================== OTP Management ====================

    // Generate OTP for password reset
    public String generateOtp(String email) throws Exception {
        Optional<Driver> optionalDriver = driverRepository.findByEmail(email);
        if (!optionalDriver.isPresent()) {
            throw new Exception("Driver not found with email: " + email);
        }

        generatedOtp = generateRandomOtp();
        otpEmail = email;
        emailService.sendOtpEmail(email, generatedOtp);

        return "OTP sent to your email.";
    }

    // Generate random OTP
    private String generateRandomOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            char letter = (char) (random.nextInt(26) + 'A');
            otp.append(letter);
        }
        return otp.toString();
    }

    // Reset password using OTP
    public String resetPassword(String otp, String newPassword) throws Exception {
        if (otp.equals(generatedOtp) && otpEmail != null) {
            Driver driver = driverRepository.findByEmail(otpEmail)
                    .orElseThrow(() -> new Exception("Driver not found."));

            driver.setPassword(passwordEncoder.encode(newPassword));
            driverRepository.save(driver);

            // Clear the OTP after successful reset
            generatedOtp = null;
            otpEmail = null;

            return "Password has been reset successfully.";
        } else {
            throw new Exception("Invalid OTP.");
        }
    }

 
  
 
    // Check if email already exists in the system
    private boolean emailExists(String email) {
        return driverRepository.findByEmail(email).isPresent();
    }

    // Method to toggle driver status (active/inactive)
    public boolean toggleDriverStatus(Long id, boolean enable) {
        Optional<Driver> driverOptional = driverRepository.findById(id);
        if (driverOptional.isPresent()) {
            Driver driver = driverOptional.get();
            driver.setStatus(enable ? "active" : "inactive");
            driverRepository.save(driver);
            return true;
        }
        return false;
    }


    public ApiResponse<String> deleteDriverById(Long id) {
        Optional<Driver> driverOptional = driverRepository.findById(id);

        if (driverOptional.isPresent()) {
            // Driver exists, perform the deletion
            driverRepository.deleteById(id);
            return new ApiResponse<>(HttpStatus.OK, "Driver deleted successfully.", "Driver deleted successfully.");
        } else {
            // Driver not found
            return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Driver not found.");
        }
    }


}