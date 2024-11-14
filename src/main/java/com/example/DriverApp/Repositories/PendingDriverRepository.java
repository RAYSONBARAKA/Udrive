package com.example.DriverApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import com.example.DriverApp.Entities.PendingDriver;

public interface PendingDriverRepository extends JpaRepository<PendingDriver, Long> {

    Optional<PendingDriver> findByEmail(String email);
}

