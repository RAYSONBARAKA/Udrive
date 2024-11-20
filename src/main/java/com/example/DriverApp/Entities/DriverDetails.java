package com.example.DriverApp.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DriverDetails {
      @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long driverId;
    private String driverName;
    private String driverPhone;   
    private Long customerId;
    private String vehicleRegistrationNumber;
    private String vehicleMake;
    private String fullName;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public Long getDriverId() {
        return driverId;
    }


    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }


    public String getDriverName() {
        return driverName;
    }


    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }


    public String getDriverPhone() {
        return driverPhone;
    }


    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }


    public Long getCustomerId() {
        return customerId;
    }


    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }


    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }


    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }


    public String getVehicleMake() {
        return vehicleMake;
    }


    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }


    public String getFullName() {
        return fullName;
    }


    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public RideRequest getRideRequest() {
        return rideRequest;
    }


    public void setRideRequest(RideRequest rideRequest) {
        this.rideRequest = rideRequest;
    }


    @ManyToOne
    @JoinColumn(name = "ride_request_id")
    private RideRequest rideRequest;   

 }

