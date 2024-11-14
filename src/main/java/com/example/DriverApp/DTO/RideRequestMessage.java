package com.example.DriverApp.DTO;

public class RideRequestMessage {
    private String type; // "request" or "accept"
    private String customerId;
    private String driverId;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public String getDriverId() {
        return driverId;
    }
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
}
