package com.papol.inventory.repository;

// WHY: Extending JpaRepository provides automatic CRUD (findAll, findById, save,
//      deleteById) without writing any SQL. This is the ABSTRACTION principle —
//      the service uses high-level methods, not low-level database code.
// WHAT: Manages Category entities in the 'categories' table.
// NOTE: No custom query methods needed — standard CRUD is sufficient for categories.

import com.papol.inventory.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {}