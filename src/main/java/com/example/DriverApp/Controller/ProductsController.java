package com.example.DriverApp.Controller;

import com.example.DriverApp.Entities.Products;
import com.example.DriverApp.Service.ProductsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/open/products")
@CrossOrigin(origins = "*")

public class ProductsController {

    private static final String UPLOAD_DIR = "C:/Users/RAYSON/Pictures/Saved Pictures/";

    @Autowired
    private ProductsService productsService;

    // Create or Update a Product with File Upload
    @PostMapping("/save")
    public ResponseEntity<Products> saveProduct(@RequestParam("serviceName") String serviceName,
                                                @RequestParam("description") String description,
                                                @RequestParam("profilePicture") MultipartFile profilePicture) throws IOException {

        // Save the uploaded file to the local directory
        String fileName = profilePicture.getOriginalFilename();
        String filePath = UPLOAD_DIR + fileName;

        File dest = new File(filePath);
        profilePicture.transferTo(dest);  // Save the file to the local machine

        // Create a new product object and set its properties
        Products product = new Products();
        product.setServiceName(serviceName);
        product.setDescription(description);
        product.setProfilePictureUrl(filePath);  // Save the file path in the DB

        // Save the product using the service
        Products savedProduct = productsService.saveProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    // Get all Products
    @GetMapping("/all")
    public ResponseEntity<List<Products>> getAllProducts() {
        List<Products> products = productsService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // Get Product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Products> getProductById(@PathVariable Long id) {
        Optional<Products> product = productsService.getProductById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a Product
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productsService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
