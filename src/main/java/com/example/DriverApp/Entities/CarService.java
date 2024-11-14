package com.example.DriverApp.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CarService {
       
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;

    private String name;
    private String vehicleType;
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
    private String description;
   @Column(nullable = false, columnDefinition = "float default 0")
private double ratePerKm = 0;


    public double getRatePerKm() {
        return ratePerKm;
    }
    public void setRatePerKm(double ratePerKm) {
        this.ratePerKm = ratePerKm;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    private Double distance;
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
   
    
   

    
    public Double getDistance() {
        return distance;
    }
    public void setDistance(Double distance) {
        this.distance = distance;
    }
 
    }


    

