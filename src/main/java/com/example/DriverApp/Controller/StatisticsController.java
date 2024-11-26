package com.example.DriverApp.Controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DriverApp.Service.StatisticsService;

@RestController
@CrossOrigin(origins = "*")

public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

   
    @GetMapping("/api/open/statistics")
    public Map<String, Object> getStatistics() {
        long approvedDrivers = statisticsService.getApprovedDriversCount();
        long activeCustomers = statisticsService.getActiveCustomersCount();
        long totalRides = statisticsService.getTotalRidesCount();
        double totalRevenue = statisticsService.getTotalRevenue();  

        return Map.of(
            "approvedDrivers", approvedDrivers,
            "activeCustomers", activeCustomers,
            "totalRides", totalRides,
            "totalRevenue", totalRevenue  
        );
    }

     @GetMapping("/api/open/statistics/service")
    public Map<String, Long> getTotalRidesByService(@RequestParam String serviceName) {
        long totalRides = statisticsService.getTotalRidesByService(serviceName);
        
        return Map.of(
            "totalRides", totalRides
        );
    }

    @GetMapping("/api/open/rides-by-service")
    public Map<String, Long> getRidesByService() {
        return statisticsService.getRidesCountByService();
    
    }
}
