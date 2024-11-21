package com.example.DriverApp.Entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
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
    private String phoneNumber;
    private String profilePictureUrl;
    private Double longitude; 
    private Double latitude;   
  

     public String getPhoneNumber() {
        return phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }


    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }


    public Double getLongitude() {
        return longitude;
    }


    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public Double getLatitude() {
        return latitude;
    }


    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    private Long customerId;
    private String vehicleRegistrationNumber;
    private String vehicleMake;
    private String fullName;

       @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;


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

