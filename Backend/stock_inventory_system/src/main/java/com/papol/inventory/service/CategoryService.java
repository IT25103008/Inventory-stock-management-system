package com.papol.inventory.service;


import com.papol.inventory.model.Category;
import com.papol.inventory.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CategoryService {

    
    @Autowired
    private CategoryRepository categoryRepository;

    
    public List<Category> getAllCategories()               { return categoryRepository.findAll(); }

    
    public Optional<Category> getCategoryById(String id)  { return categoryRepository.findById(id); }

    
    public Category addCategory(Category c)               { return categoryRepository.save(c); }

    
    public Category updateCategory(String id, Category updated) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        return categoryRepository.save(existing);
    }

    
    public void deleteCategory(String id) { categoryRepository.deleteById(id); }
}
