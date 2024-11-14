package com.example.DriverApp.DTO;

public class CarServiceResponse {
    private String vehicleType;
    private double estimatedPrice;

    public CarServiceResponse(String vehicleType, double estimatedPrice) {
        this.vehicleType = vehicleType;
        this.estimatedPrice = estimatedPrice;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public double getEstimatedPrice() {
        return estimatedPrice;
    }
}

