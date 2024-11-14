package com.example.DriverApp.DTO;

import org.springframework.http.HttpStatus;

public class ApiResponse<T> {
    private HttpStatus status;
    private T data;
    private String message;

    public ApiResponse(HttpStatus status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    // Getter and Setter methods
    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}