package com.example.DriverApp.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.DriverApp.Entities.Mover;
import com.example.DriverApp.Repositories.MoverRepository;


@Service
public class MoverService {
      private final MoverRepository moverRepository;

    @Autowired
    public MoverService(MoverRepository moverRepository) {
        this.moverRepository = moverRepository;
    }

    // Method to save a new Mover
    public Mover saveMover(Mover mover) {
        return moverRepository.save(mover);
    }

    // Method to retrieve all Movers
    public List<Mover> getAllMovers() {
        return moverRepository.findAll();
    }

    // Method to retrieve a Mover by ID
    public Optional<Mover> getMoverById(Long id) {
        return moverRepository.findById(id);
    }

      // New method to retrieve Movers by vehicleType
      public List<Mover> getMoversByVehicleType(String vehicleType) {
        return moverRepository.findByVehicleType(vehicleType);
    }
}


