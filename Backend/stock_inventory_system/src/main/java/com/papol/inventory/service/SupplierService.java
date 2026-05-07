package com.papol.inventory.service;

// WHY: SupplierService provides ABSTRACTION — the controller calls high-level methods,
//      never touches the database directly. All JPA/SQL logic is hidden here.
// WHAT: Standard CRUD operations for supplier management.

import com.papol.inventory.model.Supplier;
import com.papol.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SupplierService {

    // WHY: Dependency Injection via @Autowired — loose coupling between layers.
    @Autowired
    private SupplierRepository supplierRepository;

    // WHAT: Returns all suppliers. Empty list if none exist.
    public List<Supplier> getAllSuppliers()               { return supplierRepository.findAll(); }

    // WHAT: Find one supplier by ID.
    public Optional<Supplier> getSupplierById(String id) { return supplierRepository.findById(id); }

    // WHAT: Saves a new supplier record.
    public Supplier addSupplier(Supplier s)              { return supplierRepository.save(s); }

    // WHAT: Updates supplier details. Finds existing record first, then overwrites fields.
    public Supplier updateSupplier(String id, Supplier updated) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        existing.setName(updated.getName());
        existing.setContact(updated.getContact());
        existing.setEmail(updated.getEmail());
        existing.setAddress(updated.getAddress());
        return supplierRepository.save(existing);
    }

    // WHAT: Deletes a supplier by ID.
    public void deleteSupplier(String id)                { supplierRepository.deleteById(id); }
}