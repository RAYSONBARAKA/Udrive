package com.example.DriverApp.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "archived_notifications")
public class ArchiveNotification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long driverId;
    private String message;
    private String subject;
    private LocalDateTime date;
    private LocalDateTime createdAt;
    private String status;
    private String recipientEmail;

     public ArchiveNotification() {}

     public ArchiveNotification(Long id, Customer customer, Long driverId, String message, String subject,
                               LocalDateTime date, LocalDateTime createdAt, String status, String recipientEmail) {
        this.id = id;
        this.customerId = customer.getId();  // Assuming Customer entity has an `id` field
        this.driverId = driverId;
        this.message = message;
        this.subject = subject;
        this.date = date;
        this.createdAt = createdAt;
        this.status = status;
        this.recipientEmail = recipientEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

}
