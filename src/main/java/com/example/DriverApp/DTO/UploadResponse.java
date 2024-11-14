package com.example.DriverApp.DTO;

public class UploadResponse {
    private String message;
    private String url;

    public UploadResponse(String message, String url) {
        this.message = message;
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
