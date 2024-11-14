package com.example.DriverApp.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.DriverApp.Entities.Products;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {
}

