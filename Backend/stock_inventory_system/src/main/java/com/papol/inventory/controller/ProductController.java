package com.papol.inventory.controller;

// WHY: Controller layer — receives HTTP requests and delegates to ProductService.
//      Kept thin: no business logic here, only request/response handling.
// WHAT: REST endpoints for Product CRUD operations.
// NOTE: '@RestController' = automatic JSON serialisation of return values.

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

    // WHAT: GET /products — returns all products as JSON array (HTTP 200).
    @GetMapping("/products")
    public List<Product> getAll()                        { return productService.getAllProducts(); }

    // WHAT: GET /products/{id} — returns one product or HTTP 404 if not found.
    @GetMapping("/products/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // WHAT: POST /products — creates a new product. Returns HTTP 201 (Created).
    // NOTE: '@RequestBody Product p' tells Spring to parse the JSON body into a Product object.
    @PostMapping("/products")
    public ResponseEntity<Product> add(@RequestBody Product p) {
        return ResponseEntity.status(201).body(productService.addProduct(p));
    }

    // WHAT: PUT /products/{id} — updates product details. HTTP 200 on success, 404 on not found.
    @PutMapping("/products/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Product p) {
        try {
            return ResponseEntity.ok(productService.updateProduct(id, p));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // WHAT: DELETE /products/{id} — removes a product. Returns confirmation message.
    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}