package com.example.DriverApp.DTO;

public class CarTypeWithPriceDTO {
    private String vehicleType;
    private double price;

    public CarTypeWithPriceDTO(String vehicleType, double price) {
        this.vehicleType = vehicleType;
        this.price = price;
    }

    // Getters and Setters
    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

