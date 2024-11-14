package com.example.DriverApp.DTO;

public class CustomerInfoDTO  {

    private String fullName; // Combines first name and last name
    private String phoneNumber;

    // Constructors
    public CustomerInfoDTO(String fullName, String phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
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
}
    
