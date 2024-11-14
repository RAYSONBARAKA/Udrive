package com.example.DriverApp.DTO;

public class DriverInfoDTO {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String profilePictureUrl;
    private String vehicleType;
    private String vehicleModel;
    private String vehicleRegistrationNumber;
    private String vehicleMake;
    private double latitude;   // New field for latitude
    private double longitude;  // New field for longitude

    // Updated constructor with latitude and longitude
    public DriverInfoDTO(Long id, String fullName, String phoneNumber, String profilePictureUrl, 
                         String vehicleType, String vehicleModel, String vehicleRegistrationNumber, 
                         String vehicleMake, double latitude, double longitude) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.vehicleType = vehicleType;
        this.vehicleModel = vehicleModel;
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
        this.vehicleMake = vehicleMake;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

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

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
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
