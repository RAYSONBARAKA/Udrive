package com.example.DriverApp.Service;

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
}

