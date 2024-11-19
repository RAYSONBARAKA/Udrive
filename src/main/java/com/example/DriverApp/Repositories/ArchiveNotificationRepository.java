package com.example.DriverApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.DriverApp.Entities.ArchiveNotification;

public interface ArchiveNotificationRepository extends JpaRepository<ArchiveNotification, Long> {
    // Custom queries can be added here if needed
}
