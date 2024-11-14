package com.example.DriverApp.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity

public class RideHistory {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long rideRequestId;
    private String driverName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    private double pickupLatitude;
    private double pickupLongitude;
    private double dropOffLatitude;
    private double dropOffLongitude;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getRideRequestId() {
        return rideRequestId;
    }
    public void setRideRequestId(Long rideRequestId) {
        this.rideRequestId = rideRequestId;
    }
    public String getDriverName() {
        return driverName;
    }
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
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


    
    
}
