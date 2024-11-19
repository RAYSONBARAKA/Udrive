package com.example.DriverApp.Service;

import org.springframework.stereotype.Service;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

@Service
public class NotificationSender {

     public void sendNotification(String deviceIp, String message) {
        try {
             String notificationEndpoint = "http://" + deviceIp + ":8080"; 

            // Create a URL object from the notification endpoint
            URL url = new URL(notificationEndpoint);

            // Open an HTTP connection to the URL
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the request method to POST
            connection.setRequestMethod("POST");

            // Allow sending data in the request body
            connection.setDoOutput(true);

            // Set the content type to form-urlencoded
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Prepare the data to send in the body of the request
            String data = "message=" + message; // Include the message to notify the Android device

            // Open an OutputStream to send the data
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = data.getBytes("utf-8");
                os.write(input, 0, input.length);  // Write the data to the request body
            }

            // Get the response code from the Android device
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Disconnect the connection after the request is sent
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}