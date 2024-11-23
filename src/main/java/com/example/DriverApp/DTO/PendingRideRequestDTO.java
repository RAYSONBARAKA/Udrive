package com.example.DriverApp.DTO;


public class PendingRideRequestDTO {

    private Long requestId;
    private String customerFirstName;
    private String customerLastName;
    private Double customerLatitude;
    private Double customerLongitude;
    private String customerPhoneNumber;

    public PendingRideRequestDTO(Long requestId, String customerFirstName, String customerLastName,
                                 Double customerLatitude, Double customerLongitude, String customerPhoneNumber) {
        this.requestId = requestId;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerLatitude = customerLatitude;
        this.customerLongitude = customerLongitude;
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public Double getCustomerLatitude() {
        return customerLatitude;
    }

    public void setCustomerLatitude(Double customerLatitude) {
        this.customerLatitude = customerLatitude;
    }

    public Double getCustomerLongitude() {
        return customerLongitude;
    }

    public void setCustomerLongitude(Double customerLongitude) {
        this.customerLongitude = customerLongitude;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }
}
