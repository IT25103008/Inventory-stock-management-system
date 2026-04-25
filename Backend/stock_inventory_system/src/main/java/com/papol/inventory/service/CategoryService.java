package com.papol.inventory.service;

// WHY: CategoryService hides all database logic from the controller (ABSTRACTION).
// WHAT: Standard CRUD operations for categories — list, find, add, update, delete.

import com.papol.inventory.model.Category;
import com.papol.inventory.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CategoryService {

    // WHY: Dependency Injection — Spring provides the repository automatically.
    @Autowired
    private CategoryRepository categoryRepository;

    // WHAT: Returns all categories. Empty list if none exist.
    public List<Category> getAllCategories()               { return categoryRepository.findAll(); }

    // WHAT: Find one category by ID. Returns Optional (handles "not found" gracefully).
    public Optional<Category> getCategoryById(String id)  { return categoryRepository.findById(id); }

    // WHAT: Saves a new category to the database.
    public Category addCategory(Category c)               { return categoryRepository.save(c); }

    // WHAT: Updates category name and description. Uses orElseThrow() for safety.
    public Category updateCategory(String id, Category updated) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return categoryRepository.save(existing);
    }

    // WHAT: Deletes a category by ID.
    public void deleteCategory(String id) { categoryRepository.deleteById(id); }
}