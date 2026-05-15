package com.papol.inventory.repository;



import com.papol.inventory.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockTransactionRepository extends JpaRepository<StockTransaction, String> {


    List<StockTransaction> findByProductId(String productId);


    List<StockTransaction> findByType(String type);
}