package com.example.DriverApp.DTO;

public class RegisterResponse {
    private String employeeNumber;
    private Long adminId;

    public RegisterResponse(String employeeNumber, Long adminId) {
        this.employeeNumber = employeeNumber;
        this.adminId = adminId;
    }

    // Getters and setters
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}

