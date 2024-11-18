package com.example.DriverApp.Service;

import com.cloudinary.Cloudinary;
import com.example.DriverApp.DTO.ApiResponse;
import com.example.DriverApp.DTO.CustomerDTO;
import com.example.DriverApp.DTO.LoginResponse;
import com.example.DriverApp.Entities.Customer;
import com.example.DriverApp.Repositories.CustomerRepository;
import com.example.DriverApp.Utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    private final String customerUploadDirectory = "C:/Users/RAYSON/Pictures/CustomerPictures"; // Temporary

    // ==================== Customer Management ====================

    public Customer saveCustomer(Customer requestCustomer) {
        if (customerRepository.findByEmail(requestCustomer.getEmail()).isPresent()) {
            throw new RuntimeException("A customer with this email is already registered.");
        }
        if (customerRepository.findByPhoneNumber(requestCustomer.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("A customer with this phone number is already registered.");
        }

        Customer newCustomer = new Customer();
        newCustomer.setFirstName(requestCustomer.getFirstName());
        newCustomer.setLastName(requestCustomer.getLastName());
        newCustomer.setEmail(requestCustomer.getEmail());
        newCustomer.setPhoneNumber(requestCustomer.getPhoneNumber());
        newCustomer.setCity(requestCustomer.getCity());
        newCustomer.setDateOfBirth(requestCustomer.getDateOfBirth());

        // Password tokenization (28 days expiry)
        String passwordToken = jwtUtil.generateTokenWithExpiration(requestCustomer.getPassword(), 28);
        newCustomer.setPassword(passwordToken);

        // Activation process
        String activationCode = generateRandomCode();
        newCustomer.setActivationCode(activationCode);
        newCustomer.setActive(false);
        newCustomer.setActivationCodeGeneratedTime(LocalDateTime.now());

        // Save customer and send activation email
        Customer savedCustomer = customerRepository.save(newCustomer);
        emailService.sendRegistrationEmail(savedCustomer.getEmail(), activationCode);

        return savedCustomer;
    }

    private String generateRandomCode() {
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }

    public String activateAccount(String activationCode) {
        Optional<Customer> customerOptional = customerRepository.findByActivationCode(activationCode);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (customer.getActivationCodeGeneratedTime() != null) {
                long minutesSinceGenerated = ChronoUnit.MINUTES.between(customer.getActivationCodeGeneratedTime(), LocalDateTime.now());
                if (minutesSinceGenerated > 1440) {
                    return "Activation code expired. Request a new code.";
                }
            }

            customer.setActive(true);
            customerRepository.save(customer);
            return "Account activated successfully.";
        } else {
            return "Invalid activation code.";
        }
    }

    public LoginResponse loginCustomer(String email, String password, double latitude, double longitude) {
        Optional<Customer> customerOptional = customerRepository.findByEmail(email);
    
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
    
            if (!customer.isActive()) {
                return null; // Account is not activated
            }
    
            if (jwtUtil.validateToken(customer.getPassword(), password)) {
                customer.setLastLoginTime(LocalDateTime.now());
                customer.setLatitude(latitude);
                customer.setLongitude(longitude);
    
                String token = jwtUtil.generateTokenWithExpiration(customer.getEmail(), 28);
                customer.setToken(token);
                customerRepository.save(customer);
    
                return new LoginResponse(customer.getId(), token);
            }
        }
    
        return null;
    }

    public ApiResponse<String> updateLocation(Long customerId, double latitude, double longitude) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setLatitude(latitude);
            customer.setLongitude(longitude);
            customerRepository.save(customer);
            return new ApiResponse<>(HttpStatus.OK, null, "Location updated successfully.");
        } else {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Customer not found.");
        }
    }

    // ==================== Profile Picture ====================

    public ApiResponse<String> uploadCustomerPicture(Long customerId, MultipartFile file) {
        try {
            Optional<Customer> customerOptional = customerRepository.findById(customerId);
            if (!customerOptional.isPresent()) {
                return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Customer not found.");
            }

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("public_id", "customers/" + customerId + "_" + file.getOriginalFilename()));
            String pictureUrl = (String) uploadResult.get("secure_url");

            Customer customer = customerOptional.get();
            customer.setPicturePath(pictureUrl);
            customerRepository.save(customer);

            return new ApiResponse<>(HttpStatus.OK, pictureUrl, "Picture uploaded successfully.");
        } catch (IOException e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, null, "Error uploading picture.");
        }
    }

    public ApiResponse<byte[]> getCustomerPicture(Long customerId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (!customerOptional.isPresent()) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Customer not found");
        }

        String pictureUrl = customerOptional.get().getPicturePath();
        if (pictureUrl == null || pictureUrl.isEmpty()) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Customer picture not found");
        }

        try {
            byte[] imageBytes = fetchImageBytesFromUrl(pictureUrl);
            return new ApiResponse<>(HttpStatus.OK, imageBytes, "Customer picture retrieved successfully.");
        } catch (IOException e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, null, "Error retrieving customer picture");
        }
    }

    private byte[] fetchImageBytesFromUrl(String url) throws IOException {
        try (InputStream in = new URL(url).openStream()) {
            return in.readAllBytes();
        }
    }

    // ==================== Deactivate and Activate Account ====================

    public ApiResponse<String> deactivateAccount(Long customerId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (!customer.isActive()) {
                return new ApiResponse<>(HttpStatus.OK, null, "Account already deactivated.");
            }

            customer.setActive(false);
            customerRepository.save(customer);
            return new ApiResponse<>(HttpStatus.OK, null, "Account deactivated successfully.");
        } else {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Customer not found.");
        }
    }

    public ApiResponse<String> activateAccount(Long customerId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (customer.isActive()) {
                return new ApiResponse<>(HttpStatus.OK, null, "Account is already active.");
            }

            customer.setActive(true);
            customerRepository.save(customer);
            return new ApiResponse<>(HttpStatus.OK, null, "Account activated successfully.");
        } else {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Customer not found.");
        }
    }

    // ==================== Reset Password with OTP ====================

    public ApiResponse<String> resetPasswordWithOtp(String email, String otp, String newPassword) {
        Optional<Customer> customerOptional = customerRepository.findByEmail(email);

        if (!customerOptional.isPresent()) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Customer not found.");
        }

        Customer customer = customerOptional.get();

        if (!otp.equals(customer.getResetOtp())) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST, null, "Invalid OTP.");
        }

        String hashedPassword = jwtUtil.generateTokenWithExpiration(newPassword, 28);
        customer.setPassword(hashedPassword);
        customerRepository.save(customer);

        return new ApiResponse<>(HttpStatus.OK, null, "Password reset successful.");
    }

    public ApiResponse<String> sendPasswordResetOtp(String email) {
        Optional<Customer> customerOptional = customerRepository.findByEmail(email);

        if (!customerOptional.isPresent()) {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Customer not found.");
        }

        Customer customer = customerOptional.get();
        String otp = generateOtp();
        customer.setResetOtp(otp);
        customerRepository.save(customer);

        emailService.sendPasswordResetEmail(customer.getEmail(), otp);

        return new ApiResponse<>(HttpStatus.OK, null, "OTP sent successfully.");
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);  // Generate a 6-digit OTP
        return String.valueOf(otp);
    }

    public ApiResponse<Customer> getCustomerById(Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            return new ApiResponse<>(HttpStatus.OK, customer, "Customer retrieved successfully.");
        } else {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Customer not found.");
        }
    }

    // ==================== Other Methods ====================

    public ApiResponse<String> deleteCustomer(Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        
        if (customerOptional.isPresent()) {
            customerRepository.delete(customerOptional.get());
            return new ApiResponse<>(HttpStatus.OK, "Customer deleted successfully.", "Customer deleted successfully.");
        } else {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Customer not found.");
        }
    }

    public ApiResponse<Customer> updateCustomer(Long id, Customer updatedCustomer) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        
        if (customerOptional.isPresent()) {
            Customer existingCustomer = customerOptional.get();
            existingCustomer.setFirstName(updatedCustomer.getFirstName());
            existingCustomer.setLastName(updatedCustomer.getLastName());
            existingCustomer.setEmail(updatedCustomer.getEmail());

            Customer savedCustomer = customerRepository.save(existingCustomer);
            return new ApiResponse<>(HttpStatus.OK, savedCustomer, "Customer updated successfully.");
        } else {
            return new ApiResponse<>(HttpStatus.NOT_FOUND, null, "Customer not found.");
        }
    }

    public ApiResponse<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return new ApiResponse<>(HttpStatus.OK, customers, "All customers retrieved successfully.");
    }
}
