package com.papol.inventory.controller;

// WHY: Thin Controller — delegates all logic to SupplierService (Separation of Concerns).
// WHAT: REST endpoints for Supplier CRUD operations.

import com.papol.inventory.model.Supplier;
import com.papol.inventory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // WHAT: GET /suppliers — returns all suppliers as JSON.
    @GetMapping("/suppliers")
    public List<Supplier> getAll()                        { return supplierService.getAllSuppliers(); }

    // WHAT: GET /suppliers/{id} — returns one supplier or HTTP 404.
    @GetMapping("/suppliers/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // WHAT: POST /suppliers — creates a new supplier. Returns HTTP 201.
    @PostMapping("/suppliers")
    public ResponseEntity<Supplier> add(@RequestBody Supplier s) {
        return ResponseEntity.status(201).body(supplierService.addSupplier(s));
    }

    // WHAT: PUT /suppliers/{id} — updates a supplier. HTTP 404 if not found.
    @PutMapping("/suppliers/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Supplier s) {
        try {
            return ResponseEntity.ok(supplierService.updateSupplier(id, s));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // WHAT: DELETE /suppliers/{id} — removes a supplier.
    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}