package com.example.DriverApp.Controller;

import com.example.DriverApp.DTO.ApiResponse;
import com.example.DriverApp.DTO.CustomerDTO;
import com.example.DriverApp.DTO.LoginRequest;
import com.example.DriverApp.DTO.LoginResponse;
import com.example.DriverApp.Entities.Customer;
import com.example.DriverApp.Entities.Driver;
import com.example.DriverApp.Service.CustomerService;
import com.example.DriverApp.Service.RideService;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/open/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private RideService rideService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Customer>> saveCustomer(@RequestBody Customer customer) {
        try {
            // Save customer and handle response
            Customer savedCustomer = customerService.saveCustomer(customer);

            // Return success response
            ApiResponse<Customer> response = new ApiResponse<>(HttpStatus.CREATED, savedCustomer, "Customer created successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            // Return error response if customer already exists or other issues
            ApiResponse<Customer> response = new ApiResponse<>(HttpStatus.BAD_REQUEST, null, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // Handle general errors
            ApiResponse<Customer> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, null, "Internal server error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(@PathVariable Long id) {
        ApiResponse<Customer> response = customerService.getCustomerById(id);

        if (response.getStatus() == HttpStatus.OK) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // ==================== Delete Customer ====================

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(@PathVariable Long id) {
        ApiResponse<String> response = customerService.deleteCustomer(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    // ==================== Update Customer ====================

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @PathVariable Long id, 
            @RequestBody Customer customer) {
        ApiResponse<Customer> response = customerService.updateCustomer(id, customer);

        if (response.getStatus() == HttpStatus.OK) {
            return ResponseEntity.ok(response);
        } else {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // ==================== Get All Customers ====================

    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        ApiResponse<List<Customer>> response = customerService.getAllCustomers();
        return new ResponseEntity<>(response, response.getStatus());
    }


    @PostMapping("/activate-account")
    public ResponseEntity<ApiResponse<String>> activateAccount(@RequestParam String activationCode) {
        String message = customerService.activateAccount(activationCode);
        if (message.equals("Account activated successfully.")) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, message, "Account activated successfully."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST, message, "Account activation failed."));
    }

    @PostMapping("/login")
public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
    LoginResponse loginResponse = customerService.loginCustomer(
        loginRequest.getEmail(),
        loginRequest.getPassword(),
        loginRequest.getLatitude(),
        loginRequest.getLongitude()
    );

    if (loginResponse != null) {
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, loginResponse, "Login successful"));
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, null, "Invalid email or password, or account is not activated."));
    }
}

    @PostMapping("/{email}/{otp}/{newPassword}-reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @PathVariable String email,
            @PathVariable String otp,
            @PathVariable String newPassword) {
        ApiResponse<String> response = customerService.resetPasswordWithOtp(email, otp, newPassword);
        return new ResponseEntity<>(response, response.getStatus());
    }

    // ==================== Request Password Reset OTP ====================
    
    @PostMapping("/{email}/request")
    public ResponseEntity<ApiResponse<String>> requestPasswordResetOtp(@PathVariable String email) {
        ApiResponse<String> response = customerService.sendPasswordResetOtp(email);
        return new ResponseEntity<>(response, response.getStatus());
    }

    // ==================== Upload Customer Picture ====================
    
    @PostMapping("/{id}/upload-picture")
    public ResponseEntity<ApiResponse<String>> uploadCustomerPicture(
            @PathVariable Long id, 
            @RequestParam("file") MultipartFile file) {
        ApiResponse<String> response = customerService.uploadCustomerPicture(id, file);
        return new ResponseEntity<>(response, response.getStatus());
    }

    // ==================== Get Customer Picture ====================
    
    @GetMapping("/{customerId}/picture")
    public ResponseEntity<byte[]> getCustomerPicture(@PathVariable Long customerId) {
        ApiResponse<byte[]> response = customerService.getCustomerPicture(customerId);

        if (response.getStatus() != HttpStatus.OK) {
            return new ResponseEntity<>(null, null, response.getStatus());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(response.getData().length);
        return new ResponseEntity<>(response.getData(), headers, HttpStatus.OK);
    }

    // ==================== Activate and Deactivate Account ====================
    
    @PutMapping("/{customerId}/activate")
    public ResponseEntity<ApiResponse<String>> activateAccount(@PathVariable Long customerId) {
        ApiResponse<String> response = customerService.activateAccount(customerId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PutMapping("/{customerId}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateAccount(@PathVariable Long customerId) {
        ApiResponse<String> response = customerService.deactivateAccount(customerId);
        return new ResponseEntity<>(response, response.getStatus());
    }

    // ==================== Track Driver Location ====================
    
    // @GetMapping("/track-driver")
    // public ResponseEntity<ApiResponse<DriverLocation>> trackDriver(@RequestParam Long rideRequestId) {
    //     ApiResponse<DriverLocation> response = rideService.getDriverLocation(rideRequestId);
    //     return new ResponseEntity<>(response, response.getStatus());
    }
    

