package com.example.DriverApp.Entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pending_drivers")  

public class PendingDriver {
        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Personal Information

    private String fullName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth; 
    private String password;
    private String profilePictureUrl;


    
    
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    //Role Selection


    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Vehicle Information
    private String vehicleType; // Passenger, Cargo, Movers
    private String vehicleModel; // Sedan, SUV, 
    private String vehicleRegistrationNumber;
    private String vehicleMake;
    private String insuranceDetails;

    // Driver's License Information
    private String licenseNumber;
    private String licenseClass;
    private int drivingExperience; // the experience 

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

    public String getInsuranceDetails() {
        return insuranceDetails;
    }

    public void setInsuranceDetails(String insuranceDetails) {
        this.insuranceDetails = insuranceDetails;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
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

    public boolean isCriminalBackgroundCheck() {
        return criminalBackgroundCheck;
    }

    public void setCriminalBackgroundCheck(boolean criminalBackgroundCheck) {
        this.criminalBackgroundCheck = criminalBackgroundCheck;
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

    // Background checks
    private boolean criminalBackgroundCheck;//files

    // Payment Information
    private String mpesaNumber;
    private String bankAccountDetails;
    private String paymentPreference; // Mpesa or Bank

    // Terms and Conditions
    private boolean termsAndConditionsAgreed;

    
}
