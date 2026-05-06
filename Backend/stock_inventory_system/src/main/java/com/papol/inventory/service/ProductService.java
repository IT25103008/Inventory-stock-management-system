package com.papol.inventory.service;

// WHY: ProductService demonstrates ABSTRACTION — the controller calls simple methods
//      like getAllProducts() without knowing the database queries behind them.
// WHAT: Handles all business logic for product CRUD plus a low-stock query.
// NOTE: '@Service' marks this as a Spring-managed bean. '@Autowired' injects the repository.

import com.papol.inventory.model.Product;
import com.papol.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {

    // WHY: Dependency Injection — Spring provides the repository instance automatically.
    @Autowired
    private ProductRepository productRepository;

    // WHAT: Returns all products. Empty list if none exist (never null).
    public List<Product> getAllProducts()               { return productRepository.findAll(); }

    // WHAT: Find one product by ID. Returns Optional (maybe empty if ID not found).
    public Optional<Product> getProductById(String id) { return productRepository.findById(id); }

    // WHAT: Saves a new product to the database.
    public Product addProduct(Product p)               { return productRepository.save(p); }

    // WHAT: Updates an existing product. Finds the original first, then overwrites fields.
    // NOTE: Uses orElseThrow() — defensive programming. Throws if product ID doesn't exist.
    public Product updateProduct(String id, Product updated) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setName(updated.getName());
        existing.setCategoryId(updated.getCategoryId());
        existing.setSupplierId(updated.getSupplierId());
        existing.setQuantity(updated.getQuantity());
        existing.setPrice(updated.getPrice());
        return productRepository.save(existing);
    }

    // WHAT: Deletes a product by its ID.
    public void deleteProduct(String id)               { productRepository.deleteById(id); }

    // WHY: This method supports the Reports module. It uses the custom repository
    //      method findByQuantityLessThan() to find products below a given stock level.
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByQuantityLessThan(threshold);
    }
}