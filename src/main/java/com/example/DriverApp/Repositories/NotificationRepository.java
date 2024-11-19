package com.example.DriverApp.Repositories;

import java.util.List;
import com.example.DriverApp.Entities.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find notifications for a specific customer and their read status
    List<Notification> findByCustomerIdAndStatus(Long customerId, String status);

    // Find notifications for a specific driver and their read status
    List<Notification> findByDriverIdAndStatus(Long driverId, String status);

    // Find all notifications for a specific customer (unread by default)
    List<Notification> findByCustomerId(Long customerId);

    // Find all notifications for a specific driver (unread by default)
    List<Notification> findByDriverId(Long driverId);

    List<Notification> findByRecipientEmail(String recipientEmail);
 

}



