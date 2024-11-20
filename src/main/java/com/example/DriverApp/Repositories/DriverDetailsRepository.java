package com.example.DriverApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DriverApp.Entities.DriverDetails;

@Repository
public interface DriverDetailsRepository extends JpaRepository<DriverDetails, Long> {
 }