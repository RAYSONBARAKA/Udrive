// package com.example.DriverApp.Controller;

// import java.time.LocalDateTime;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.DriverApp.Entities.Customer;
// import com.example.DriverApp.Repositories.NotificationRepository;
// import com.example.DriverApp.Service.NotificationService;
// @RestController
// public class NotificationController {

//     @Autowired
//     private NotificationService notificationService;

//     @PostMapping("/sendRideAcceptedNotification")
//     public String sendNotification(Long customerId, String message) {
//         // Assuming you have a method to fetch customer by ID
//         Customer customer = customerService.getCustomerById(customerId);
        
//         // Call the method to send the notification
//         notificationService.sendRideAcceptedNotification(customer, message);
//         return "Notification Sent!";
//     }
// }