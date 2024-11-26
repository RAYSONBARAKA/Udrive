package com.example.DriverApp.DTO;



public class RideHistoryDTO {
    private double distance;
    private double totalAmount;
    private double price;
    private String serviceName;
    private String vehicleType;
    private double pickupLatitude;
    private double pickupLongitude;
    private double dropOffLatitude;
    private double dropOffLongitude;
    private String driverName;

    public RideHistoryDTO(double distance, double totalAmount, double price, String serviceName, String vehicleType,
                          double pickupLatitude, double pickupLongitude, double dropOffLatitude, double dropOffLongitude,
                          String driverName) {
        this.distance = distance;
        this.totalAmount = totalAmount;
        this.price = price;
        this.serviceName = serviceName;
        this.vehicleType = vehicleType;
        this.pickupLatitude = pickupLatitude;
        this.pickupLongitude = pickupLongitude;
        this.dropOffLatitude = dropOffLatitude;
        this.dropOffLongitude = dropOffLongitude;
        this.driverName = driverName;
    }

    // Getters and setters for all fields
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

    public double getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public double getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public double getDropOffLatitude() {
        return dropOffLatitude;
    }

    public void setDropOffLatitude(double dropOffLatitude) {
        this.dropOffLatitude = dropOffLatitude;
    }

    public double getDropOffLongitude() {
        return dropOffLongitude;
    }

    public void setDropOffLongitude(double dropOffLongitude) {
        this.dropOffLongitude = dropOffLongitude;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
