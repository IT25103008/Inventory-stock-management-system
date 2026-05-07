package com.papol.inventory.controller;


import com.papol.inventory.model.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

   
    @GetMapping("/suppliers")
    public List<Supplier> getAll()                        { return supplierService.getAllSuppliers(); }

   
    @GetMapping("/suppliers/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        return supplierService.getSupplierById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

   
    @PostMapping("/suppliers")
    public ResponseEntity<Supplier> add(@RequestBody Supplier s) {
        return ResponseEntity.status(201).body(supplierService.addSupplier(s));
    }

   
    @PutMapping("/suppliers/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Supplier s) {
        try {
            return ResponseEntity.ok(supplierService.updateSupplier(id, s));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

   
    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}
