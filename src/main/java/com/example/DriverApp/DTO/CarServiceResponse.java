package com.example.DriverApp.DTO;

public class CarServiceResponse {
    private String vehicleType; // Represents a single vehicle type
    private double ratePerKm;   // Rate per km for the vehicle
    private double estimatedPrice; // Estimated price for the trip

    // Constructor
    public CarServiceResponse(String vehicleType, double ratePerKm, double estimatedPrice) {
        this.vehicleType = vehicleType;
        this.ratePerKm = ratePerKm;
        this.estimatedPrice = estimatedPrice;
    }

    // Getters and setters
    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getRatePerKm() {
        return ratePerKm;
    }

    public void setRatePerKm(double ratePerKm) {
        this.ratePerKm = ratePerKm;
    }

    public double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
}
