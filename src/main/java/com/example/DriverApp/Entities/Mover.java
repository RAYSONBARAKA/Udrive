package com.example.DriverApp.Entities;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Mover {


     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 


    private String vehicleType;
    private String pickupLocation;
    private String dropoffLocation;
    private LocalDate movingDate;
    private LocalTime movingTime;
    public String getVehicleType() {
        return vehicleType;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    public String getPickupLocation() {
        return pickupLocation;
    }
    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }
    public String getDropoffLocation() {
        return dropoffLocation;
    }
    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }
    public LocalDate getMovingDate() {
        return movingDate;
    }
    public void setMovingDate(LocalDate movingDate) {
        this.movingDate = movingDate;
    }
    public LocalTime getMovingTime() {
        return movingTime;
    }
    public void setMovingTime(LocalTime movingTime) {
        this.movingTime = movingTime;
    }


    
}
