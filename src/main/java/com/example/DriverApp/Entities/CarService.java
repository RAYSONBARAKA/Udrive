package com.example.DriverApp.Entities;

import jakarta.persistence.*;

@Entity
public class CarService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;

    private String name;

    @ManyToOne
    @JoinColumn(name = "driver_id")  // Assuming you have a driver table
    private Driver driver;

    public Driver getDriver() {
        return driver;
    }


    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    private String vehicleType;  

    private String description;

    @Column(nullable = false, columnDefinition = "float default 0")
    private double ratePerKm = 0;

    private Double distance;

    public CarService() {}


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRatePerKm() {
        return ratePerKm;
    }

    public void setRatePerKm(double ratePerKm) {
        this.ratePerKm = ratePerKm;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
