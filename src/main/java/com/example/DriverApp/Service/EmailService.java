package com.example.DriverApp.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    

    // Send registration email with the activation code
    public void sendRegistrationEmail(String recipientEmail, String activationCode) {
        String htmlContent = generateRegistrationEmailContent(activationCode);
        sendEmail(recipientEmail, "Activate Your Account", htmlContent);
    }

    

    // Send deletion email to notify the user
    public void sendDeletionEmail(String recipientEmail, String customerName) {
        String htmlContent = generateDeletionEmailContent(customerName);
        sendEmail(recipientEmail, "Account Deletion Notification", htmlContent);
    }

    // Send OTP email for password reset
    public void sendOtpEmail(String recipientEmail, String otp) {
        String htmlContent = generateOtpEmailContent(otp);
        sendEmail(recipientEmail, "Your Password Reset OTP", htmlContent);
    }

    // Send password reset email with an OTP
    public void sendPasswordResetEmail(String recipientEmail, String otp) {
        String htmlContent = generatePasswordResetEmailContent(otp);
        sendEmail(recipientEmail, "Password Reset Request", htmlContent);
    }

    // Send approval email to notify a driver that they have been approved
    public void sendApprovalEmail(String recipientEmail, String driverName) {
        String htmlContent = generateApprovalEmailContent(driverName);
        sendEmail(recipientEmail, "Your Account Has Been Approved", htmlContent);
    }

    // New method: Send rejection email to notify a driver that they have been rejected
    public void sendRejectionEmail(String recipientEmail, String driverName) {
        String htmlContent = generateRejectionEmailContent(driverName);
        sendEmail(recipientEmail, "Driver Registration Rejected", htmlContent);
    }

    // Utility method to send email
    private void sendEmail(String recipientEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // Enable HTML content
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Optionally throw a custom exception or handle the error gracefully
        }
    }

    // Generate the registration email content with an activation code
    private String generateRegistrationEmailContent(String activationCode) {
        return "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #e2f0d9; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #2c7a41;'>Welcome to Udrive!</h2>" +
                "<p style='font-size: 16px;'>Please activate your account using the code below:</p>" +
                "<div style='background-color: #3cba5c; color: white; padding: 10px; border-radius: 5px;'>" +
                "<h3 style='margin: 0;'>Activation Code: " + activationCode + "</h3></div>" +
                "<p>This code will expire in 24 hours.</p>" +
                "<p>Thank you for choosing Udrive!</p>" +
                "</div></body></html>";
    }

    // Generate the email content for account deletion notification
    private String generateDeletionEmailContent(String customerName) {
        return "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #f8d7da; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #721c24;'>Account Deletion Notice</h2>" +
                "<p style='font-size: 16px;'>Dear " + customerName + ",</p>" +
                "<p>We regret to inform you that your account has been successfully deleted from our system.</p>" +
                "<p>Thank you for your time with Udrive.</p>" +
                "</div></body></html>";
    }

    // Generate the OTP email content for password reset
    private String generateOtpEmailContent(String otp) {
        return "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #cce5ff; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #004085;'>Password Reset OTP</h2>" +
                "<div style='background-color: #004085; color: white; padding: 10px; border-radius: 5px;'>" +
                "<p style='margin: 0;'>Your OTP for password reset is: <strong>" + otp + "</strong></p></div>" +
                "<p style='font-size: 16px;'>Please note that this OTP is valid for 2 minutes.</p>" +
                "</div></body></html>";
    }

    // Generate the email content for password reset request
    private String generatePasswordResetEmailContent(String otp) {
        return "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #cce5ff; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #004085;'>Password Reset Request</h2>" +
                "<p>We received a request to reset your password. Use the OTP below to reset it:</p>" +
                "<div style='background-color: #004085; color: white; padding: 10px; border-radius: 5px;'>" +
                "<h3 style='margin: 0;'>OTP: " + otp + "</h3></div>" +
                "<p style='font-size: 16px;'>This OTP is valid for 2 minutes.</p>" +
                "<p>If you did not request a password reset, please ignore this email.</p>" +
                "</div></body></html>";
    }

    // Generate the approval email content for notifying the driver
    private String generateApprovalEmailContent(String driverName) {
        return "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #d4edda; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #155724;'>Congratulations, " + driverName + "!</h2>" +
                "<p style='font-size: 16px;'>Your account has been successfully approved.</p>" +
                "<p>You can now log in to the Udrive platform and start using your account.</p>" +
                "<p>Thank you for joining TripStar!</p>" +
                "</div></body></html>";
    }

    // Generate the rejection email content for notifying the driver
    private String generateRejectionEmailContent(String driverName) {
        return "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #f8d7da; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #721c24;'>Application Rejected</h2>" +
                "<p style='font-size: 16px;'>Dear " + driverName + ",</p>" +
                "<p>We regret to inform you that your driver registration application has been rejected.</p>" +
                "<p>If you have any questions, please contact support.</p>" +
                "</div></body></html>";
    }


    public void sendRideAcceptedEmail(String recipientEmail, String customerName, String driverName) {
        String subject = "Your Ride Request Has Been Accepted";
        String htmlContent = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background-color: #d4edda; padding: 20px; border-radius: 8px;'>" +
                "<h2 style='color: #155724;'>Ride Accepted!</h2>" +
                "<p>Dear " + customerName + ",</p>" +
                "<p>Your ride request has been accepted by " + driverName + ".</p>" +
                "<p>Your driver will arrive shortly. Please be ready at your pickup location.</p>" +
                "<p>Thank you for using Udrive!</p>" +
                "</div></body></html>";
        sendEmail(recipientEmail, subject, htmlContent);
    }
    


   
    }



