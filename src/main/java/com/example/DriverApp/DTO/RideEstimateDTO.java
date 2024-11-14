package com.example.DriverApp.DTO;

public class RideEstimateDTO {
    private String vehicleType;
    private double estimatedPrice;

    public RideEstimateDTO(String vehicleType, double estimatedPrice) {
        this.vehicleType = vehicleType;
        this.estimatedPrice = estimatedPrice;
    }

    // Getters and Setters
    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }
}
