package com.example.DriverApp.Entities;

import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
 import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long driverId;
    private Long customerId;
    private String message;
    private String subject;
    private LocalDateTime date;
    private String recipientEmail;
 
    private String status; // Ensure this matches your query parameter

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    // Default constructor
    public Notification() {}

    // Constructor that accepts message, subject, and date
    public Notification(String message, String subject, LocalDateTime date) {
        this.message = message;
        this.subject = subject;
        this.date = date;
    }

    // Constructor with driverId and customerId (optional, depending on your use case)
    public Notification(Long driverId, Long customerId, String message, String subject, LocalDateTime date) {
        this.driverId = driverId;
        this.customerId = customerId;
        this.message = message;
        this.subject = subject;
        this.date = date;
    }

    // Getters and setters for all fields
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}