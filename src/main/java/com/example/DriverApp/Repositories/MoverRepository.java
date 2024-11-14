package com.example.DriverApp.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DriverApp.Entities.Mover;

@Repository
public interface MoverRepository extends JpaRepository<Mover, Long> {

    List<Mover> findByVehicleType(String vehicleType); 

}


