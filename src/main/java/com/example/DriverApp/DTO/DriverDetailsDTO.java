package com.example.DriverApp.DTO;

import com.example.DriverApp.Entities.DriverLocationEntity;

public class DriverDetailsDTO {
    
    private String fullName;
    private String phoneNumber;
    private String profilePictureUrl;
    private String mpesaNumber;
    private String role;
    
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleRegistrationNumber;
    private String vehicleType;

    private DriverLocationEntity location;

    public DriverDetailsDTO(String fullName, String phoneNumber, String profilePictureUrl, 
                            String mpesaNumber, String role, String vehicleMake, String vehicleModel,
                            String vehicleRegistrationNumber, String vehicleType, DriverLocationEntity location) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.mpesaNumber = mpesaNumber;
        this.role = role;
        this.vehicleMake = vehicleMake;
        this.vehicleModel = vehicleModel;
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
        this.vehicleType = vehicleType;
        this.location = location;
    }

    // Getters and setters for each field

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public String getMpesaNumber() { return mpesaNumber; }
    public void setMpesaNumber(String mpesaNumber) { this.mpesaNumber = mpesaNumber; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getVehicleMake() { return vehicleMake; }
    public void setVehicleMake(String vehicleMake) { this.vehicleMake = vehicleMake; }

    public String getVehicleModel() { return vehicleModel; }
    public void setVehicleModel(String vehicleModel) { this.vehicleModel = vehicleModel; }

    public String getVehicleRegistrationNumber() { return vehicleRegistrationNumber; }
    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) { this.vehicleRegistrationNumber = vehicleRegistrationNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public DriverLocationEntity getLocation() { return location; }
    public void setLocation(DriverLocationEntity location) { this.location = location; }
}
