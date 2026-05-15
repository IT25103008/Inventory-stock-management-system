package com.papol.inventory.repository;



import com.papol.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    
    List<Product> findByQuantityLessThan(int threshold);   
}
