package com.example.DriverApp.Service;


import com.example.DriverApp.Entities.Products;
import com.example.DriverApp.Repositories.ProductsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    @Autowired
    private ProductsRepository productsRepository;

    // Create or Update Product
    public Products saveProduct(Products product) {
        return productsRepository.save(product);
    }

    // Get all Products
    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    // Get a Product by ID
    public Optional<Products> getProductById(Long id) {
        return productsRepository.findById(id);
    }

    // Delete a Product by ID
    public void deleteProduct(Long id) {
        productsRepository.deleteById(id);
    }
}
