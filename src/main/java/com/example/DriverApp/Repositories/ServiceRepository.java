package com.example.DriverApp.Repositories;

import com.example.DriverApp.Entities.ServiceEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

    ServiceEntity findByServiceName(String serviceName);


}
