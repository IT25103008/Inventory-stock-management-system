package com.papol.inventory.repository;

// WHY: Extending JpaRepository gives us all standard CRUD operations for free.
//      This is ABSTRACTION — the service layer calls simple methods like findAll()
//      without knowing the SQL queries that run behind the scenes.
// WHAT: Manages Product entities. JpaRepository<Product, String> means the
//       entity type is Product and the primary key type is String.

import com.papol.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    // WHY: Custom query method — Spring Data auto-generates:
    //      SELECT * FROM products WHERE quantity < ?
    //      This is used by the Reports module to find low-stock items.
    // NOTE: The method name follows Spring Data's naming convention:
    //       findBy + FieldName + LessThan = automatic WHERE clause.
    List<Product> findByQuantityLessThan(int threshold);   // for low-stock report
}