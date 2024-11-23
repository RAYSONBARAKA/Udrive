package com.example.DriverApp.Entities;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.persistence.Column;

@Entity
@Table(name = "ride_requests")
public class RideRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false) 
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "driver_id") 
    private Driver driver;

    @ManyToOne
    private CarService carService;

    private String pickupLocation;
    private String dropOffLocation;

    @Column(nullable = true) // Allow price to be null
    private Double price; // Changed to Double to allow nulls

    @Version
    private Long version;

    private String vehicleType;
    private String serviceName;  

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String driverName;
    private double pickupLatitude;
    private double pickupLongitude;
     private Float calculatedPrice;


    public Float getCalculatedPrice() {
        return calculatedPrice;
    }

    public void setCalculatedPrice(Float calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

     
    

    private double dropOffLatitude;
    private double dropOffLongitude;

    private LocalDateTime requestTime;
    private LocalDateTime acceptanceTime;
    private String status;

    @Column(name = "service_id", nullable = false) // Ensure this field is not nullable
    private Long serviceId; // Add this field for the service ID

    @Column(name = "total_price") // New field for total price
    private double totalPrice; // Add totalPrice field

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
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

    public double getDropOffLatitude() {
        return dropOffLatitude;
    }

    public void setDropOffLatitude(double dropOffLatitude) {
        this.dropOffLatitude = dropOffLatitude;
    }

    public double getDropOffLongitude() {
        return dropOffLongitude;
    }

    public void setDropOffLongitude(double dropOffLongitude) {
        this.dropOffLongitude = dropOffLongitude;
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

    public Long getServiceId() {
        return serviceId; // Getter for serviceId
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId; // Setter for serviceId
    }

    public Double getPrice() {
        return price; // Getter for price (can be null)
    }

    public void setPrice(Double price) {
        this.price = price; // Setter for price (can be null)
    }

    public double getTotalPrice() {
        return totalPrice; // Getter for total price
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice; // Setter for total price
    }

    // Method to get the Customer ID
    public Long getCustomerId() {
        return customer != null ? customer.getId() : null; 
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public CarService getCarService() {
        return carService;
    }

    public void setCarService(CarService carService) {
        this.carService = carService;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public double getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public double getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }
}
