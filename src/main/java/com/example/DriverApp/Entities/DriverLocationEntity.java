package com.example.DriverApp.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class DriverLocationEntity {

    @Id
    private Long driverId;  
    private double latitude;
    private double longitude;

    // Getters and setters
    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
