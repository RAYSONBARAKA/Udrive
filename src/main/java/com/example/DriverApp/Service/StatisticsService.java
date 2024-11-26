package com.example.DriverApp.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.DriverApp.Repositories.CustomerRepository;
import com.example.DriverApp.Repositories.DriverRepository;
import com.example.DriverApp.Repositories.RideHistoryRepository;

@Service
public class StatisticsService {
      private final DriverRepository driverRepository;
    private final CustomerRepository customerRepository;
    private final RideHistoryRepository rideHistoryRepository;

    public StatisticsService(DriverRepository driverRepository, CustomerRepository customerRepository, RideHistoryRepository rideHistoryRepository) {
        this.driverRepository = driverRepository;
        this.customerRepository = customerRepository;
        this.rideHistoryRepository = rideHistoryRepository;
    }

    public long getApprovedDriversCount() {
        return driverRepository.countApprovedDrivers();
    }

    public long getActiveCustomersCount() {
        return customerRepository.countActiveCustomers();
    }

    public long getTotalRidesCount() {
        return rideHistoryRepository.countTotalRides();
    }

    
    public long getTotalRidesByService(String serviceName) {
        return rideHistoryRepository.countByServiceName(serviceName);   
    }


    public double getTotalRevenue() {
        return rideHistoryRepository.calculateTotalRevenue();
    }

 
    public Map<String, Long> getRidesCountByService() {
        // Fetch the result from the repository
        List<Object[]> results = rideHistoryRepository.countRidesByService();

        // Convert the list of Object[] into a map of serviceName -> count
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],  
                        result -> (Long) result[1]     
                ));
    }

}
    

