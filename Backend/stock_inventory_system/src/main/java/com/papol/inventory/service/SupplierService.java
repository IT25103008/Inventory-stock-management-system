package com.papol.inventory.service;



import com.papol.inventory.model.Supplier;
import com.papol.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SupplierService {

 
    @Autowired
    private SupplierRepository supplierRepository;

   
    public List<Supplier> getAllSuppliers()               { return supplierRepository.findAll(); }

    
    public Optional<Supplier> getSupplierById(String id) { return supplierRepository.findById(id); }

   
    public Supplier addSupplier(Supplier s)              { return supplierRepository.save(s); }

    
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
