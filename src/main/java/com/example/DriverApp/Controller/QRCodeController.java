package com.example.DriverApp.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DriverApp.Service.QRCodeService;


@RestController
public class QRCodeController {
    
    private final QRCodeService qrCodeService;

    public QRCodeController(QRCodeService qrCodeService) {
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/generate-qrcode")
    public ResponseEntity<String> generateQRCode(@RequestParam String fileUrl) {
        try {
            String qrCodeImage = qrCodeService.generateQRCode(fileUrl, 350, 350);
            return ResponseEntity.ok(qrCodeImage);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to generate QR code");
        }
    }

}
