package com.example.DriverApp.DTO;

public class RideRequestWithCustomerDTO {
    private Long requestId;
    private CustomerInfoDTO customerInfo; // Use the customer infomation DTO
    private String pickupLocation;
    private String dropoffLocation; 

    // Constructors
    public RideRequestWithCustomerDTO(Long requestId, CustomerInfoDTO customerInfo, String pickupLocation, String dropoffLocation) {
        this.requestId = requestId;
        this.customerInfo = customerInfo;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
    }

    // Getters and Setters
    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public CustomerInfoDTO getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfoDTO customerInfo) {
        this.customerInfo = customerInfo;
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
}
