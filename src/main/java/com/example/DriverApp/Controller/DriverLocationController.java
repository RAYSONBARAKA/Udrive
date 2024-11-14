package com.example.DriverApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DriverApp.Service.RideService;


@RestController
@RequestMapping("/driver")
public class DriverLocationController {

        @Autowired
    private RideService rideService;

      @PostMapping("/update-location")
    public void updateDriverLocation(
            @RequestParam Long driverId, 
            @RequestParam double latitude, 
            @RequestParam double longitude) {

        rideService.updateDriverLocation(driverId, latitude, longitude);
    }
}
