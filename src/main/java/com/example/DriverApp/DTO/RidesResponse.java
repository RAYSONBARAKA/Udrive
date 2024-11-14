package com.example.DriverApp.DTO;

import java.util.List;

public class RidesResponse {

    private String message;
    private List<RideRequestWithCustomerDTO> rides;
    private Double estimatedPrice;
    private Double timeToPickup;
    private Double timeToDestination;

    // Constructor for message and rides
    public RidesResponse(String message, List<RideRequestWithCustomerDTO> rides) {
        this.message = message;
        this.rides = rides;
    }

    // Constructor for message with additional estimated details
    public RidesResponse(String message, Double estimatedPrice, Double timeToPickup, Double timeToDestination) {
        this.message = message;
        this.estimatedPrice = estimatedPrice;
        this.timeToPickup = timeToPickup;
        this.timeToDestination = timeToDestination;
    }

    // Getters and setters for each field
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<RideRequestWithCustomerDTO> getRides() {
        return rides;
    }

    public void setRides(List<RideRequestWithCustomerDTO> rides) {
        this.rides = rides;
    }

    public Double getEstimatedPrice() {
        return estimatedPrice;
    }

    public void setEstimatedPrice(Double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public Double getTimeToPickup() {
        return timeToPickup;
    }

    public void setTimeToPickup(Double timeToPickup) {
        this.timeToPickup = timeToPickup;
    }

    public Double getTimeToDestination() {
        return timeToDestination;
    }

    public void setTimeToDestination(Double timeToDestination) {
        this.timeToDestination = timeToDestination;
    }
}
