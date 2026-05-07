package com.papol.inventory.controller;


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

    
    @GetMapping("/categories")
    public List<Category> getAll()                         { return categoryService.getAllCategories(); }

    
    @GetMapping("/categories/{id}")
    public ResponseEntity<?> getOne(@PathVariable String id) {
        return categoryService.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
    @PostMapping("/categories")
    public ResponseEntity<Category> add(@RequestBody Category c) {
        return ResponseEntity.status(201).body(categoryService.addCategory(c));
    }

    
    @PutMapping("/categories/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Category c) {
        try {
            return ResponseEntity.ok(categoryService.updateCategory(id, c));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}
