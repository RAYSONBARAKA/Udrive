package com.example.DriverApp.DTO;

import java.util.List;

import com.example.DriverApp.Entities.CarService;

public class UpdateResponse {
     private List<CarService> updatedServices;
    private String message;

    public UpdateResponse(List<CarService> updatedServices, String message) {
        this.updatedServices = updatedServices;
        this.message = message;
    }

    public List<CarService> getUpdatedServices() {
        return updatedServices;
    }

    public void setUpdatedServices(List<CarService> updatedServices) {
        this.updatedServices = updatedServices;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
