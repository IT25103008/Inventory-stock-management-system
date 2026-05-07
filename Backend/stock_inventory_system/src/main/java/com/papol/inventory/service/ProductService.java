package com.papol.inventory.service;



import com.papol.inventory.model.Product;
import com.papol.inventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {

    
    @Autowired
    private ProductRepository productRepository;

    
    public List<Product> getAllProducts()               { return productRepository.findAll(); }

    
    public Optional<Product> getProductById(String id) { return productRepository.findById(id); }

    
    public Product addProduct(Product p)               { return productRepository.save(p); }

    
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

    
    public void deleteProduct(String id)               { productRepository.deleteById(id); }

    
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByQuantityLessThan(threshold);
    }
}
