package com.papol.inventory.repository;

// WHY: Standard JpaRepository interface — gives free CRUD operations via ABSTRACTION.
// WHAT: Manages Supplier entities in the 'suppliers' table.
// NOTE: No custom query methods needed — standard CRUD is sufficient for suppliers.

import com.papol.inventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, String> {}