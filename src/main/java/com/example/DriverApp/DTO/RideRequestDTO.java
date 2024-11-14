package com.example.DriverApp.DTO;
import java.time.LocalDateTime;

public class RideRequestDTO {

    private Long id;
    private String customerFullName;
    private String pickupLocation;
    private String dropOffLocation;
    private LocalDateTime requestTime;
    private LocalDateTime acceptanceTime;
    private String status;

    // Constructor with all fields
    public RideRequestDTO(Long id, String customerFullName, String pickupLocation, String dropOffLocation,
                          LocalDateTime requestTime, LocalDateTime acceptanceTime, String status) {
        this.id = id;
        this.customerFullName = customerFullName;
        this.pickupLocation = pickupLocation;
        this.dropOffLocation = dropOffLocation;
        this.requestTime = requestTime;
        this.acceptanceTime = acceptanceTime;
        this.status = status;
    }

    // Getters and setters (optional, depending on how you use this DTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropOffLocation() {
        return dropOffLocation;
    }

    public void setDropOffLocation(String dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public LocalDateTime getAcceptanceTime() {
        return acceptanceTime;
    }

    public void setAcceptanceTime(LocalDateTime acceptanceTime) {
        this.acceptanceTime = acceptanceTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}