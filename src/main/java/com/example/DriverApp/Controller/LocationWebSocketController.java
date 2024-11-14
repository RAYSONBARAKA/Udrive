package com.example.DriverApp.Controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.example.DriverApp.Entities.CustomerLocation;
@CrossOrigin(origins = "*")

@Controller
public class LocationWebSocketController {

    @MessageMapping("/updateLocation")
    @SendTo("/topic/locations")
    public CustomerLocation updateLocation(CustomerLocation location) {
        // Handle location update
        return location;
    }
}