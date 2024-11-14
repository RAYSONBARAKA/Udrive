package com.example.DriverApp.PushNotification;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class FirebaseNotificationService {

    private static final String FCM_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "BNZxx2iNwto2fxsAedYenglYcFw6PdsF0WeMMcRCBfi_vXCJRwRjHZjCG644MilFaE2JFZy-UuQ_YNXM9prXt2E"; 

    public void sendPushNotification(String deviceToken, String messageBody) {
        // Set headers for the FCM request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(SERVER_KEY);

        // Create the notification payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("to", deviceToken);

        Map<String, String> notification = new HashMap<>();
        notification.put("title", "Ride Accepted");
        notification.put("body", messageBody);
        payload.put("notification", notification);

        // Build the HTTP request entity
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        // Send the request to FCM
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(FCM_URL, request, String.class);
            System.out.println("FCM response: " + response.getStatusCode() + " - " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error sending FCM notification: " + e.getMessage());
        }
    }
}
