package com.example.DriverApp.DTO;

import java.util.List;

public class CarServiceResponse {
    private List<String> vehicleType; // Represents multiple vehicle types
    private double ratePerKm;         // Rate per km for the vehicle
    private double estimatedPrice;    // Estimated price for the trip

    // Constructor
    public CarServiceResponse(List<String> vehicleType, double ratePerKm, double estimatedPrice) {
        this.vehicleType = vehicleType;
        this.ratePerKm = ratePerKm;
        this.estimatedPrice = estimatedPrice;
    }

    // Getters and setters
    public List<String> getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(List<String> vehicleType) {
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
