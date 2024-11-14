package com.example.DriverApp.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DriverApp.Entities.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Admin findByEmail(String email);
    Admin findByEmail(String email);



    

}

        
