package com.example.DriverApp.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.DriverApp.Entities.Mover;
import com.example.DriverApp.Service.MoverService;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/open/movers")
@CrossOrigin(origins = "*")

public class MoverController {
     private final MoverService moverService;

    @Autowired
    public MoverController(MoverService moverService) {
        this.moverService = moverService;
    }

    // Endpoint to create a new Mover
    @PostMapping 
    public ResponseEntity<Mover> createMover(@RequestBody Mover mover) {
        Mover savedMover = moverService.saveMover(mover);
        return new ResponseEntity<>(savedMover, HttpStatus.CREATED);
    }

    // Endpoint to get all Movers
    @GetMapping
    public ResponseEntity<List<Mover>> getAllMovers() {
        List<Mover> movers = moverService.getAllMovers();
        return new ResponseEntity<>(movers, HttpStatus.OK);
    }

    // Endpoint to get a Mover by ID
    @GetMapping("/{id}")
    public ResponseEntity<Mover> getMoverById(@PathVariable Long id) {
        Optional<Mover> mover = moverService.getMoverById(id);
        return mover.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/by-vehicle-type")
    public ResponseEntity<List<Mover>> getMoversByVehicleType(@RequestParam String vehicleType) {
        List<Mover> movers = moverService.getMoversByVehicleType(vehicleType);
        return new ResponseEntity<>(movers, HttpStatus.OK);
    }
}



