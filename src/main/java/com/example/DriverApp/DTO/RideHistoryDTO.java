package com.example.DriverApp.DTO;

public class RideHistoryDTO {
    
    private double distance;
    private double totalAmount;
    private double price;
    private String serviceName;
    private String vehicleType;

    // Constructors
    public RideHistoryDTO(double distance, double totalAmount, double price, String serviceName, String vehicleType) {
        this.distance = distance;
        this.totalAmount = totalAmount;
        this.price = price;
        this.serviceName = serviceName;
        this.vehicleType = vehicleType;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}
