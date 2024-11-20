package com.example.DriverApp.Entities;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "drivers")
public class Driver {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(nullable = false)
    private String status;
    // Personal Information


    private Double latitude; 
    private Boolean isActive;

    private String serviceName;   
    private Double longitude;   
    private boolean active; 
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private String fullName;
    private String email;
    private String phoneNumber;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private LocalDate dateOfBirth;
    private boolean available;
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    private String password;
    private String profilePictureUrl;
    private String role;

    // Vehicle Information
    private String vehicleType;
    private String vehicleModel;

   
    private String vehicleRegistrationNumber;
    private String vehicleMake;
    
    private String insuranceDetailsUrl;
   
    public double getLatitude() {
        return latitude;
    }

    public String getInsuranceDetailsUrl() {
        return insuranceDetailsUrl;
    }

    public void setInsuranceDetailsUrl(String insuranceDetailsUrl) {
        this.insuranceDetailsUrl = insuranceDetailsUrl;
    }

    public String getLicenseNumberUrl() {
        return licenseNumberUrl;
    }

    public void setLicenseNumberUrl(String licenseNumberUrl) {
        this.licenseNumberUrl = licenseNumberUrl;
    }

    public String getCriminalBackgroundCheckUrl() {
        return criminalBackgroundCheckUrl;
    }

    public void setCriminalBackgroundCheckUrl(String criminalBackgroundCheckUrl) {
        this.criminalBackgroundCheckUrl = criminalBackgroundCheckUrl;
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

    // Driver's License Information
    private String licenseNumberUrl;
    private String licenseClass;
    private int drivingExperience;

    // Background checks
    private String criminalBackgroundCheckUrl;

    // Payment Information
    private String mpesaNumber;
    private String bankAccountDetails;
    private String paymentPreference;

    // Terms and Conditions
    private boolean termsAndConditionsAgreed;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

   
    
    public String getLicenseClass() {
        return licenseClass;
    }

    public void setLicenseClass(String licenseClass) {
        this.licenseClass = licenseClass;
    }

    public int getDrivingExperience() {
        return drivingExperience;
    }

    public void setDrivingExperience(int drivingExperience) {
        this.drivingExperience = drivingExperience;
    }


    

    public String getMpesaNumber() {
        return mpesaNumber;
    }

    public void setMpesaNumber(String mpesaNumber) {
        this.mpesaNumber = mpesaNumber;
    }

    public String getBankAccountDetails() {
        return bankAccountDetails;
    }

    public void setBankAccountDetails(String bankAccountDetails) {
        this.bankAccountDetails = bankAccountDetails;
    }

    public String getPaymentPreference() {
        return paymentPreference;
    }

    public void setPaymentPreference(String paymentPreference) {
        this.paymentPreference = paymentPreference;
    }

    public boolean isTermsAndConditionsAgreed() {
        return termsAndConditionsAgreed;
    }

    public void setTermsAndConditionsAgreed(boolean termsAndConditionsAgreed) {
        this.termsAndConditionsAgreed = termsAndConditionsAgreed;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
