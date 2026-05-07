package com.papol.inventory.repository;



import com.papol.inventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, String> {}
