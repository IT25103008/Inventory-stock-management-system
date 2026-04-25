package com.papol.inventory.controller;

// WHY: Thin Controller — delegates all logic to CategoryService (Separation of Concerns).
// WHAT: REST endpoints for Category CRUD operations.

import com.papol.inventory.model.Category;
import com.papol.inventory.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // WHAT: GET /categories — returns all categories as JSON.
    @GetMapping("/categories")
    public List<Category> getAll()                         { return categoryService.getAllCategories(); }

    // WHAT: GET /categories/{id} — returns one category or HTTP 404.
    @GetMapping("/categories/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // WHAT: POST /categories — creates a new category. Returns HTTP 201.
    @PostMapping("/categories")
    public ResponseEntity<Category> add(@RequestBody Category c) {
        return ResponseEntity.status(201).body(categoryService.addCategory(c));
    }

    // WHAT: PUT /categories/{id} — updates a category. HTTP 404 if not found.
    @PutMapping("/categories/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Category c) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, c));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // WHAT: DELETE /categories/{id} — removes a category.
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}