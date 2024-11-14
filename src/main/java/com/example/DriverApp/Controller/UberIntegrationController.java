package com.example.DriverApp.Controller;

import com.example.DriverApp.Service.TokenStorage;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.logging.Level;
import java.util.logging.Logger;
@CrossOrigin(origins = "*")

 @Controller
public class UberIntegrationController {
    private static final String CLIENT_ID = "ouKFOLPcxPlWj4Kq0b1qu6JQG6-r92Zp";
    private static final String CLIENT_SECRET = "gj5nk8iheDQdEADfu2eIYFWfnS4sGiyeBiZq29iz"; // Store securely
    private static final String REDIRECT_URI = "https://0f60-41-90-101-26.ngrok-free.app/uber/callback"; 
    private static final String TOKEN_URL = "https://auth.uber.com/oauth/v2/token";
    
    private static final Logger LOGGER = Logger.getLogger(UberIntegrationController.class.getName());

    @Autowired
    private TokenStorage tokenStorage;

    // Step 1: Redirect to Uber Authorization Page
    @GetMapping("/api/open/uber/auth")
    public ResponseEntity<Void> authorizeUber() {
        String scope = "partner.accounts partner.trips partner.payments";
        String uberAuthUrl = "https://auth.uber.com/oauth/v2/authorize?response_type=code&client_id=" 
                             + CLIENT_ID + "&scope=" + scope + "&redirect_uri=" + REDIRECT_URI;

        // Redirect the user to the Uber authorization URL
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", uberAuthUrl).build();
    }

    // Step 2: Callback for Uber OAuth2, Exchange code for access token
    @GetMapping("/api/open/uber/callback")
    public ResponseEntity<Object> uberCallback(@RequestParam("code") String code) {
        RestTemplate restTemplate = new RestTemplate();

        // Prepare the request to exchange the code for an access token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", CLIENT_ID);
        body.add("client_secret", CLIENT_SECRET);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", REDIRECT_URI);
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // Send the POST request to get the access token
            ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, requestEntity, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                String accessToken = extractAccessToken(response.getBody());
                if (accessToken != null) {
                    tokenStorage.setAccessToken(accessToken);  // Store the token
                    LOGGER.info("Access Token Received: " + accessToken);
                    return ResponseEntity.ok("Access Token: " + accessToken);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to extract access token.");
                }
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Error from Uber API: " + response.getBody());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception occurred during token exchange", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occurred: " + e.getMessage());
        }
    }

    // Extract access token from the response body
    private String extractAccessToken(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.path("access_token").asText(null); // Returns null if the field is not found
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to parse response body", e);
            return null; // Return null if there's an issue
        }
    }

    // Step 3: Get Driver Profile using the Access Token
    @GetMapping("/api/open/uber/driver")
    public ResponseEntity<String> getDriverProfile(@RequestParam("accessToken") String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Content-Type", "application/json");
        headers.set("Accept-Language", "en_US");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Make the request to get the driver's profile
            String url = "https://api.uber.com/v1/partners/me";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Error from Uber API: " + response.getBody());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch driver profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch driver profile.");
        }
    }

    // Step 4: Get Driver's Last Ten Trips using the Access Token
    @GetMapping("/api/open/uber/driver/trips")
    public ResponseEntity<String> getDriverTrips(@RequestParam("accessToken") String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Make the request to get the driver's trip data
            String url = "https://api.uber.com/v1/partners/trips";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Error from Uber API: " + response.getBody());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to fetch driver trips", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch driver trips.");
        }
    }

    // Step 5: Handle Webhook Notifications
    @PostMapping("/api/open/webhook/uber")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) {
        // Process incoming webhook payload
        LOGGER.info("Webhook received: " + payload);
        
        // Process the trip or other events contained in the payload
        return ResponseEntity.ok("Webhook processed successfully");
    }
}
