package com.papol.inventory.controller;

import com.papol.inventory.model.Product;
import com.papol.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    
    @GetMapping("/products")
    public List<Product> getAll()                        { return productService.getAllProducts(); }

    
    @GetMapping("/products/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
    @PostMapping("/products")
    public ResponseEntity<Product> add(@RequestBody Product p) {
        return ResponseEntity.status(201).body(productService.addProduct(p));
    }

    
    @PutMapping("/products/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Product p) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, p));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}
