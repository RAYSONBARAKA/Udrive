package com.example.DriverApp.Service;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.DriverApp.Entities.Notification;
import com.example.DriverApp.Repositories.NotificationRepository;
import com.example.DriverApp.Entities.Customer;
import com.example.DriverApp.Entities.Driver;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

     public Notification createNotification(Customer customer, Driver driver, String message) {
        Notification notification = new Notification();
        notification.setCustomer(customer);
        notification.setDriver(driver);  
        notification.setMessage(message);
        notification.setTimestamp(LocalDateTime.now());
        notification.setStatus("unread");  
        
        // Save and return the notification
        return notificationRepository.save(notification);
    }
}
