package com.example.DriverApp.Repositories;

import java.util.List;
import java.util.Optional;

import com.example.DriverApp.Entities.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findTopByCustomerIdOrderByDateDesc(Long customerId);
 

}



