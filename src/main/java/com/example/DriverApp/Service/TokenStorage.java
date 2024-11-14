package com.example.DriverApp.Service;

import org.springframework.stereotype.Service;

@Service
public class TokenStorage {
    private String accessToken;

    // Set the access token
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    // Get the access token
    public String getAccessToken() {
        return accessToken;
    }

    // Clear the access token (optional)
    public void clearToken() {
        this.accessToken = null;
    }

    // Check if token is available
    public boolean hasToken() {
        return accessToken != null;
    }
}
