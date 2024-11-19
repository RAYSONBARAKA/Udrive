package com.example.DriverApp.Entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@CrossOrigin("*")
@Entity
@Table(name ="Customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
     private String token; 
    private String firstName;
    private String notificationEndpoint;
    public String getNotificationEndpoint() {
        return notificationEndpoint;
    }
    public void setNotificationEndpoint(String notificationEndpoint) {
        this.notificationEndpoint = notificationEndpoint;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    @Column(nullable = true)
    private String passwordResetOtp;
    private LocalDateTime activationCodeGeneratedTime;
    private String resetOtp;  // To store the reset OTP
    private Long customerId;

    private String picturePath;

    public String getPicturePath() {
        return picturePath;
    }
    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
    private LocalDateTime otpGeneratedTime;
    public String getResetOtp() {
    return resetOtp;


}
public void setResetOtp(String resetOtp) {
    this.resetOtp = resetOtp;
}
public LocalDateTime getOtpGeneratedTime() {
    return otpGeneratedTime;
}
public void setOtpGeneratedTime(LocalDateTime otpGeneratedTime) {
    this.otpGeneratedTime = otpGeneratedTime;
}
private LocalDateTime lastLoginTime;
    private String activationCode; 
    public String getActivationCode() {
        return activationCode;
    }
    public LocalDateTime getActivationCodeGeneratedTime() {
        return activationCodeGeneratedTime;
    }
    public void setActivationCodeGeneratedTime(LocalDateTime activationCodeGeneratedTime) {
        this.activationCodeGeneratedTime = activationCodeGeneratedTime;
    }
    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }
    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    private boolean isActive; 

    private String lastName;

    private double latitude;
    private double longitude;
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
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private String phoneNumber;
    private String city;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate localDateTime) {
        this.dateOfBirth = localDateTime;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }
    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }
    public String getPasswordResetOtp() {
        return passwordResetOtp;
    }
    public void setPasswordResetOtp(String passwordResetOtp) {
        this.passwordResetOtp = passwordResetOtp;
    }
    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    



    
}